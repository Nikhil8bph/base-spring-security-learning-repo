package com.example.userauthservice.facade;

import com.example.userauthservice.dto.RoleDTO;
import com.example.userauthservice.entity.Role;
import com.example.userauthservice.mapper.RoleMapper;
import com.example.userauthservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleFacade {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public Set<RoleDTO> getDefaultRoles() {
        Set<Role> role = roleRepository.findByDefaultRoleTrue();
        return roleMapper.toDTO(role);
    }

    public RoleDTO createRole(RoleDTO roleDTO) {
        var role = roleMapper.toEntity(roleDTO);
        var savedRole = roleRepository.save(role);
        return roleMapper.toDTO(savedRole);
    }

    public RoleDTO updateRole(Long roleId, RoleDTO roleDTO) {
        Role role = roleRepository.findById(roleId).orElseThrow();
        if (roleDTO.getName() != null && !roleDTO.getName().equalsIgnoreCase(role.getName())
                && roleRepository.existsByNameIgnoreCaseAndIdNot(roleDTO.getName(), roleId)) {
            throw new IllegalArgumentException("Role already exists with name: %s".formatted(roleDTO.getName()));
        }
        if (roleDTO.getName() != null) role.setName(roleDTO.getName());
        if (roleDTO.getDescription() != null) role.setDescription(roleDTO.getDescription());
        if (roleDTO.getDefaultRole() != null) role.setDefaultRole(roleDTO.getDefaultRole());
        var savedRole = roleRepository.save(role);
        return roleMapper.toDTO(savedRole);
    }

    public RoleDTO getRole(Long roleId) {
        var role = roleRepository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("Role not found with id: %s".formatted(roleId)));
        return roleMapper.toDTO(role);
    }

    public Set<RoleDTO> getRoles() {
        var roles = roleRepository.findAll();
        return roleMapper.toDTO(roles);
    }

    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow();
        role.delete("System");
        roleRepository.save(role);
    }

    public boolean roleExists(String roleName) {
        var role = roleRepository.findByNameIgnoreCase(roleName);
        return role.isPresent();
    }
}
