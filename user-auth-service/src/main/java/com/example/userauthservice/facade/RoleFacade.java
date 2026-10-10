package com.example.userauthservice.facade;

import com.example.userauthservice.dto.RoleDTO;
import com.example.userauthservice.entity.Role;
import com.example.userauthservice.exception.ResourceConflictException;
import com.example.userauthservice.exception.ResourceNotFoundException;
import com.example.userauthservice.mapper.RoleMapper;
import com.example.userauthservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleFacade {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Value("${app.default-role-name:ADMIN}")
    private String bootstrapRoleName;

    public Set<RoleDTO> getDefaultRoles() {
        Set<Role> roles = roleRepository.findByDefaultRoleTrueAndDeletedFalseAndActiveTrue().stream()
                .filter(role -> !isPrivilegedRole(role.getName()))
                .collect(Collectors.toSet());
        return roleMapper.toDTO(roles);
    }

    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        String name = normalizeName(roleDTO.getName());
        validateSignupDefault(name, roleDTO.getDefaultRole());
        if (roleRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceConflictException("Role already exists with name: " + name);
        }
        var role = roleMapper.toEntity(roleDTO);
        role.setName(name);
        role.setDefaultRole(Boolean.TRUE.equals(roleDTO.getDefaultRole()));
        var savedRole = roleRepository.save(role);
        return roleMapper.toDTO(savedRole);
    }

    @Transactional
    public RoleDTO updateRole(Long roleId, RoleDTO roleDTO) {
        Role role = findRole(roleId);
        String name = roleDTO.getName() == null ? role.getName() : normalizeName(roleDTO.getName());
        Boolean signupDefault = roleDTO.getDefaultRole() == null ? role.getDefaultRole() : roleDTO.getDefaultRole();
        validateSignupDefault(name, signupDefault);
        if (roleRepository.existsByNameIgnoreCaseAndIdNot(name, roleId)) {
            throw new ResourceConflictException("Role already exists with name: " + name);
        }
        role.setName(name);
        if (roleDTO.getDescription() != null) role.setDescription(roleDTO.getDescription());
        if (roleDTO.getDefaultRole() != null) role.setDefaultRole(roleDTO.getDefaultRole());
        var savedRole = roleRepository.save(role);
        return roleMapper.toDTO(savedRole);
    }

    public RoleDTO getRole(Long roleId) {
        var role = findRole(roleId);
        return roleMapper.toDTO(role);
    }

    public RoleDTO getRoleByName(String name) {
        Role role = roleRepository.findByNameIgnoreCase(name).orElseThrow(
                () -> new ResourceNotFoundException("Role not found with name: " + name));
        return roleMapper.toDTO(role);
    }

    public Set<RoleDTO> getRoles() {
        var roles = roleRepository.findByDeletedFalseAndActiveTrue();
        return roleMapper.toDTO(roles);
    }

    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(
                () -> new ResourceNotFoundException("Role not found with id: " + roleId));
        if (Boolean.TRUE.equals(role.getDeleted())) {
            return;
        }
        role.delete("System");
        roleRepository.save(role);
    }

    public boolean roleExists(String roleName) {
        return roleRepository.existsByNameIgnoreCase(roleName);
    }

    private Role findRole(Long id) {
        return roleRepository.findById(id)
                .filter(role -> !Boolean.TRUE.equals(role.getDeleted()) && Boolean.TRUE.equals(role.getActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name is required");
        }
        return name.strip().toUpperCase(Locale.ROOT);
    }

    private boolean isPrivilegedRole(String name) {
        String normalized = normalizeName(name).replaceFirst("^ROLE_", "");
        return normalized.equals("ADMIN") || normalized.equals("MODERATOR")
                || normalized.equals(normalizeName(bootstrapRoleName).replaceFirst("^ROLE_", ""));
    }

    private void validateSignupDefault(String name, Boolean signupDefault) {
        if (Boolean.TRUE.equals(signupDefault) && isPrivilegedRole(name)) {
            throw new IllegalArgumentException("Privileged roles cannot be assigned by public signup");
        }
    }
}
