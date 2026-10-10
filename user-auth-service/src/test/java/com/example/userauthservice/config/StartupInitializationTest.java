package com.example.userauthservice.config;

import com.example.userauthservice.dto.RoleDTO;
import com.example.userauthservice.dto.UserDTO;
import com.example.userauthservice.entity.Role;
import com.example.userauthservice.entity.User;
import com.example.userauthservice.facade.RoleFacade;
import com.example.userauthservice.facade.UserFacade;
import com.example.userauthservice.mapper.RoleMapper;
import com.example.userauthservice.mapper.UserMapper;
import com.example.userauthservice.repository.RoleRepository;
import com.example.userauthservice.repository.UserRepository;
import com.example.userauthservice.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.open-in-view=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.default-role=true",
        "app.default-role-name=ADMIN",
        "app.default-role-description=Default admin role",
        "app.default-user=admin",
        "app.default-email=admin@example.com",
        "app.default-phone=+919876543210",
        "app.default-password=test-password"
})
@Import({StartupInitialization.class, UserFacade.class, RoleFacade.class,
        StartupInitializationTest.MapperConfiguration.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class StartupInitializationTest {
    @Autowired
    private StartupInitialization initializer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private RoleFacade roleFacade;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void repeatedStartupCreatesOnlyOneUserAndRole() throws Exception {
        initializer.run(new DefaultApplicationArguments());
        User firstUser = userRepository.findByUsername("admin").orElseThrow();

        initializer.run(new DefaultApplicationArguments());

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(roleRepository.count()).isEqualTo(2);
        User user = userRepository.findByUsername("admin").orElseThrow();
        assertThat(user.getId()).isEqualTo(firstUser.getId());
        assertThat(user.getEmail()).isEqualTo("admin@example.com");
        assertThat(user.getMobile()).isEqualTo("+919876543210");
        assertThat(user.getPassword()).isEqualTo(firstUser.getPassword());
        assertThat(passwordEncoder.matches("test-password", user.getPassword())).isTrue();
        assertThat(user.getRoles()).extracting(Role::getName).containsExactly("ADMIN");
        assertThat(new UserDetailsImpl(user).getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "email", "mobile"})
    void existingUniqueIdentifierPreventsInsertionAndPreservesAccount(String matchingField) throws Exception {
        User existing = new User();
        existing.setUsername(matchingField.equals("username") ? "admin" : "existing-user");
        existing.setEmail(matchingField.equals("email") ? "admin@example.com" : "changed@example.com");
        existing.setMobile(matchingField.equals("mobile") ? "+919876543210" : "+910000000000");
        existing.setPassword("existing-password-hash");
        existing.setActive(false);
        userRepository.saveAndFlush(existing);

        initializer.run(new DefaultApplicationArguments());
        initializer.run(new DefaultApplicationArguments());

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(roleRepository.count()).isEqualTo(2);
        User retained = userRepository.findByUsername(existing.getUsername()).orElseThrow();
        assertThat(retained.getId()).isEqualTo(existing.getId());
        assertThat(retained.getUsername()).isEqualTo(existing.getUsername());
        assertThat(retained.getEmail()).isEqualTo(existing.getEmail());
        assertThat(retained.getMobile()).isEqualTo(existing.getMobile());
        assertThat(retained.getPassword()).isEqualTo("existing-password-hash");
        assertThat(retained.getActive()).isFalse();
        if (matchingField.equals("username")) {
            assertThat(retained.getRoles()).extracting(Role::getName).containsExactly("ADMIN");
        } else {
            assertThat(retained.getRoles()).isEmpty();
        }
    }

    @Test
    void repairsAdminRoleWhilePreservingExistingRolesAndPassword() throws Exception {
        RoleDTO readerRole = new RoleDTO();
        readerRole.setName("READER");
        readerRole.setDefaultRole(false);
        RoleDTO savedRole = roleFacade.createRole(readerRole);
        UserDTO existing = new UserDTO();
        existing.setUsername("admin");
        existing.setEmail("changed@example.com");
        existing.setMobile("+910000000000");
        existing.setRoles(Set.of(savedRole));
        UserDTO savedUser = userFacade.saveUser(existing, "existing-password");
        String originalPassword = userRepository.findByUsername("admin").orElseThrow().getPassword();

        initializer.run(new DefaultApplicationArguments());
        initializer.run(new DefaultApplicationArguments());

        User retained = userRepository.findByUsername("admin").orElseThrow();
        assertThat(retained.getId()).isEqualTo(savedUser.getId());
        assertThat(retained.getPassword()).isEqualTo(originalPassword);
        assertThat(retained.getEmail()).isEqualTo("changed@example.com");
        assertThat(retained.getRoles()).extracting(Role::getName).containsExactlyInAnyOrder("ADMIN", "READER");
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(roleRepository.count()).isEqualTo(3);
    }

    @Test
    void assignsConfiguredRoleEvenWhenItIsNotASignupDefault() throws Exception {
        RoleDTO adminRole = new RoleDTO();
        adminRole.setName("ADMIN");
        adminRole.setDefaultRole(false);
        RoleDTO savedRole = roleFacade.createRole(adminRole);

        initializer.run(new DefaultApplicationArguments());

        User admin = userRepository.findByUsername("admin").orElseThrow();
        assertThat(admin.getRoles()).extracting(Role::getId).containsExactly(savedRole.getId());
        assertThat(roleRepository.findById(savedRole.getId()).orElseThrow().getDefaultRole()).isFalse();
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class MapperConfiguration {
        @Bean
        UserMapper userMapper() {
            return Mappers.getMapper(UserMapper.class);
        }

        @Bean
        RoleMapper roleMapper() {
            return Mappers.getMapper(RoleMapper.class);
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
