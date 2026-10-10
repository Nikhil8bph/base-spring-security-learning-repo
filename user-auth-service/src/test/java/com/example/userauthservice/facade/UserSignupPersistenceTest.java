package com.example.userauthservice.facade;

import com.example.sharedkernel.config.security.JwtUtils;
import com.example.userauthservice.dto.RoleDTO;
import com.example.userauthservice.dto.UserDTO;
import com.example.userauthservice.dto.request.SignUpRequest;
import com.example.userauthservice.entity.Role;
import com.example.userauthservice.entity.User;
import com.example.userauthservice.mapper.RoleMapper;
import com.example.userauthservice.mapper.UserMapper;
import com.example.userauthservice.mapper.UserRequestMapper;
import com.example.userauthservice.mapper.UserResponseMapper;
import com.example.userauthservice.repository.RoleRepository;
import com.example.userauthservice.repository.UserRepository;
import com.example.userauthservice.security.UserDetailsServiceImpl;
import com.example.userauthservice.service.impl.UserServiceImpl;
import com.example.userauthservice.validator.SignUpRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@DataJpaTest(properties = {"spring.jpa.open-in-view=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
@Import({UserFacade.class, RoleFacade.class, UserServiceImpl.class, SignUpRequestValidator.class,
        UserDetailsServiceImpl.class, UserSignupPersistenceTest.TestBeans.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserSignupPersistenceTest {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserFacade userFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void signupReusesExistingDefaultRolesForMultipleUsers() {
        Role defaultRole = createRole("USER", true);
        Role secondDefaultRole = createRole("READER", true);
        createRole("ADMIN", false);

        var firstResponse = userService.userSignUp(signUpRequest("alice", "+14155552671"));
        var secondResponse = userService.userSignUp(signUpRequest("bob", "+14155552672"));

        assertThat(firstResponse.getId()).isNotEqualTo(secondResponse.getId());
        assertThat(userRepository.count()).isEqualTo(2);
        assertThat(roleRepository.count()).isEqualTo(3);
        for (String username : new String[]{"alice", "bob"}) {
            User saved = userRepository.findByUsername(username).orElseThrow();
            assertThat(saved.getRoles()).extracting(Role::getId)
                    .containsExactlyInAnyOrder(defaultRole.getId(), secondDefaultRole.getId());
            assertThat(passwordEncoder.matches("StrongPass123!", saved.getPassword())).isTrue();
            assertThat(userDetailsService.loadUserByUsername(saved.getEmail()).getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactlyInAnyOrder("ROLE_USER", "ROLE_READER");
        }
    }

    @Test
    void savingUserDoesNotOverwriteSharedRoleMetadataFromDto() {
        Role role = createRole("USER", true);
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName("stale-role-name");
        roleDTO.setDescription("stale description");
        roleDTO.setDefaultRole(false);
        UserDTO userDTO = userDTO(roleDTO);

        UserDTO saved = userFacade.saveUser(userDTO, "StrongPass123!");

        assertThat(saved.getRoles()).extracting(RoleDTO::getName).containsExactly("USER");
        Role retained = roleRepository.findById(role.getId()).orElseThrow();
        assertThat(retained.getName()).isEqualTo("USER");
        assertThat(retained.getDescription()).isEqualTo("Original description");
        assertThat(retained.getDefaultRole()).isTrue();
        assertThat(roleRepository.count()).isEqualTo(1);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {Long.MAX_VALUE})
    void invalidRoleDoesNotCreateUserOrRole(Long roleId) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(roleId);
        roleDTO.setName("UNSAVED_ROLE");

        assertThatThrownBy(() -> userFacade.saveUser(userDTO(roleDTO), "StrongPass123!"))
                .isInstanceOf(roleId == null ? IllegalArgumentException.class : com.example.userauthservice.exception.ResourceNotFoundException.class)
                .hasMessageContaining("Role");
        assertThat(userRepository.count()).isZero();
        assertThat(roleRepository.count()).isZero();
    }

    private Role createRole(String name, boolean defaultRole) {
        Role role = new Role();
        role.setName(name);
        role.setDescription("Original description");
        role.setDefaultRole(defaultRole);
        return roleRepository.saveAndFlush(role);
    }

    private SignUpRequest signUpRequest(String name, String mobile) {
        SignUpRequest request = new SignUpRequest();
        request.setEmail(name + "@example.com");
        request.setMobile(mobile);
        request.setPassword("StrongPass123!");
        request.setSignUpUsing("EMAIL");
        return request;
    }

    private UserDTO userDTO(RoleDTO role) {
        UserDTO user = new UserDTO();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setMobile("+14155552671");
        user.setRoles(Set.of(role));
        return user;
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestBeans {
        @Bean
        UserMapper userMapper() {
            return Mappers.getMapper(UserMapper.class);
        }

        @Bean
        RoleMapper roleMapper() {
            return Mappers.getMapper(RoleMapper.class);
        }

        @Bean
        UserRequestMapper userRequestMapper() {
            return Mappers.getMapper(UserRequestMapper.class);
        }

        @Bean
        UserResponseMapper userResponseMapper() {
            return Mappers.getMapper(UserResponseMapper.class);
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        AuthenticationManager authenticationManager() {
            return mock(AuthenticationManager.class);
        }

        @Bean
        JwtUtils jwtUtils() {
            return mock(JwtUtils.class);
        }
    }
}
