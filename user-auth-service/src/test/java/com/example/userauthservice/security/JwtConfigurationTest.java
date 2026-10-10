package com.example.userauthservice.security;

import com.example.sharedkernel.config.security.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.io.UncheckedIOException;

import static org.assertj.core.api.Assertions.assertThat;

class JwtConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(context -> {
                try {
                    var sources = new YamlPropertySourceLoader()
                            .load("application", new ClassPathResource("application.yaml"));
                    sources.forEach(source -> context.getEnvironment().getPropertySources().addLast(source));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                // Exercise the checked-in defaults independently of the developer's environment.
                context.getEnvironment().getPropertySources().remove("systemEnvironment");
                context.getEnvironment().getPropertySources().remove("systemProperties");
            })
            .withBean(JwtUtils.class);

    @Test
    void configuredDefaultCanSignAndVerifyAccessAndRefreshTokens() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertTokenRoundTrip(context.getBean(JwtUtils.class));
        });
    }

    @Test
    void base64EnvironmentOverrideCanSignAndVerifyTokens() {
        var key = Jwts.SIG.HS256.key().build();
        contextRunner.withPropertyValues("JWT_SECRET=" + Encoders.BASE64.encode(key.getEncoded()))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    JwtUtils jwtUtils = context.getBean(JwtUtils.class);
                    assertThat(jwtUtils.getSecretKey().getEncoded()).isEqualTo(key.getEncoded());
                    assertTokenRoundTrip(jwtUtils);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"plain-text-secret-with-hyphens", "c2hvcnQ=", ""})
    void invalidSecretFailsAtStartupWithConfigurationMessage(String secret) {
        contextRunner.withPropertyValues("JWT_SECRET=" + secret).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasStackTraceContaining(
                    "app.jwt.secret (JWT_SECRET) must be a Base64-encoded key of at least 32 bytes");
        });
    }

    private void assertTokenRoundTrip(JwtUtils jwtUtils) {
        var user = User.withUsername("alice").password("unused").roles("USER", "ADMIN").build();
        String accessToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        assertThat(jwtUtils.validateToken(accessToken)).isTrue();
        assertThat(jwtUtils.isTokenValid(accessToken, user)).isTrue();
        assertThat(jwtUtils.extractUsername(accessToken)).isEqualTo("alice");
        Claims claims = Jwts.parser().verifyWith(jwtUtils.getSecretKey()).build()
                .parseSignedClaims(accessToken).getPayload();
        assertThat(claims.get("roles")).asList().containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        assertThat(jwtUtils.validateToken(refreshToken)).isTrue();
        assertThat(jwtUtils.isTokenValid(refreshToken, user)).isFalse();
        assertThat(claims.get("token_use", String.class)).isEqualTo("access");
        assertThat(jwtUtils.extractUsername(refreshToken)).isEqualTo("alice");
        assertThat(jwtUtils.extractExpiration(refreshToken)).isAfter(jwtUtils.extractExpiration(accessToken));
    }
}
