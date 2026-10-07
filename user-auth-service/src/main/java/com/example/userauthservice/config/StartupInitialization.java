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

@Configuration
@RequiredArgsConstructor
public class StartupInitialization implements ApplicationRunner {
    private final RoleFacade roleFacade;
    private final UserFacade userFacade;

    @Value("${app.default-role}")
    private Boolean defaultRole;

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
    public void run(ApplicationArguments args) throws Exception {
        createDefaultRoles();
        createDefaultUser();
    }

    private void createDefaultRoles() {
        if (!roleFacade.roleExists(defaultRoleName)) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setName(defaultRoleName);
            roleDTO.setDescription(defaultRoleDescription);
            roleDTO.setDefaultRole(defaultRole);
            roleFacade.createRole(roleDTO);
        }
    }

    private void createDefaultUser() {
        if (!userFacade.userExistsByEmailOrMobile(defaultPhone, defaultEmail)) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(defaultUser);
            userDTO.setEmail(defaultEmail);
            userDTO.setMobile(defaultPhone);
            userFacade.saveUser(userDTO, defaultPassword);
        }
    }

}
