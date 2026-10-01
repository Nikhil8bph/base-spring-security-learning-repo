package com.example.userauthservice.dto;

import com.example.sharedkernel.dto.BaseAuditDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO extends BaseAuditDTO {
    private String name;
    private String description;
    private Boolean defaultRole;
}
