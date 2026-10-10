package com.example.userauthservice.config;

import com.example.userauthservice.dto.RoleDTO;
import com.example.userauthservice.dto.UserDTO;
import com.example.userauthservice.facade.RoleFacade;
import com.example.userauthservice.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class StartupInitialization implements ApplicationRunner {
    private final RoleFacade roleFacade;
    private final UserFacade userFacade;

    @Value("${app.signup-role-name:USER}")
    private String signupRoleName;

    @Value("${app.default-role-name}")
    private String defaultRoleName;

    @Value("${app.default-role-description}")
    private String defaultRoleDescription;

    @Value("${app.default-user}")
    private String defaultUser;

    @Value("${app.default-email}")
    private String defaultEmail;

    @Value("${app.default-phone}")
    private String defaultPhone;

    @Value("${app.default-password}")
    private String defaultPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        createDefaultRoles();
        createDefaultUser();
        userFacade.assignRoleToUserIfPresent(defaultUser, defaultRoleName);
    }

    private void createDefaultRoles() {
        if (!roleFacade.roleExists(defaultRoleName)) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setName(defaultRoleName);
            roleDTO.setDescription(defaultRoleDescription);
            roleDTO.setDefaultRole(false);
            roleFacade.createRole(roleDTO);
        } else {
            RoleDTO role = roleFacade.getRoleByName(defaultRoleName);
            if (Boolean.TRUE.equals(role.getDefaultRole())) {
                role.setDefaultRole(false);
                roleFacade.updateRole(role.getId(), role);
            }
        }
        if (signupRoleName.equalsIgnoreCase(defaultRoleName)) {
            throw new IllegalStateException("Signup role must differ from the bootstrap admin role");
        }
        if (!roleFacade.roleExists(signupRoleName)) {
            RoleDTO role = new RoleDTO();
            role.setName(signupRoleName);
            role.setDescription("Default role for registered users");
            role.setDefaultRole(true);
            roleFacade.createRole(role);
        } else {
            RoleDTO role = roleFacade.getRoleByName(signupRoleName);
            if (!Boolean.TRUE.equals(role.getDefaultRole())) {
                role.setDefaultRole(true);
                roleFacade.updateRole(role.getId(), role);
            }
        }
    }

    private void createDefaultUser() {
        if (!userFacade.userExistsByUsernameOrEmailOrMobile(defaultUser, defaultEmail, defaultPhone)) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(defaultUser);
            userDTO.setEmail(defaultEmail);
            userDTO.setMobile(defaultPhone);
            userFacade.saveUser(userDTO, defaultPassword);
        }
    }

}
