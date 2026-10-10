package com.example.userauthservice.security;

import com.example.userauthservice.entity.Role;
import com.example.userauthservice.entity.User;
import com.example.userauthservice.repository.RoleRepository;
import com.example.userauthservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {"spring.jpa.open-in-view=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
@Import(UserDetailsServiceImpl.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserDetailsServiceImplTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @ParameterizedTest
    @CsvSource({"username,true", "email,true", "mobile,true",
            "username,false", "email,false", "mobile,false"})
    void authoritiesAreAvailableAfterLookupSessionCloses(String identifierType, boolean hasRoles) {
        String suffix = UUID.randomUUID().toString();
        User user = new User();
        user.setUsername("user-" + suffix);
        user.setEmail(suffix + "@example.com");
        user.setMobile("mobile-" + suffix);
        user.setPassword("encoded-password");
        if (hasRoles) {
            Role userRole = new Role();
            userRole.setName("USER");
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            user.getRoles().add(userRole);
            user.getRoles().add(adminRole);
        }
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            roleRepository.saveAll(user.getRoles());
            userRepository.saveAndFlush(user);
        });

        String identifier = switch (identifierType) {
            case "email" -> user.getEmail();
            case "mobile" -> user.getMobile();
            default -> user.getUsername();
        };
        UserDetails principal = userDetailsService.loadUserByUsername(identifier);

        // No test transaction may keep the lookup's persistence context alive.
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
        assertThat(principal.getUsername()).isEqualTo(user.getUsername());
        assertThat(principal.getPassword()).isEqualTo("encoded-password");
        assertThat(principal.isEnabled()).isTrue();
        if (hasRoles) {
            assertThat(principal.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                    .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        } else {
            assertThat(principal.getAuthorities()).isEmpty();
        }
    }

    @Test
    void unknownIdentifierIsRejected() {
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing-user"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
