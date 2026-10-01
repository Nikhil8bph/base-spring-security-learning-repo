package com.example.userauthservice.dto;

import com.example.sharedkernel.dto.BaseAuditDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends BaseAuditDTO {
    private String username;
    private String email;
    private String mobile;
    private Set<RoleDTO> roles;
}
