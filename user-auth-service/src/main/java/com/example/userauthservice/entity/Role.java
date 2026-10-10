package com.example.userauthservice.entity;

import com.example.sharedkernel.entity.BaseAuditEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseAuditEntity {
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    private Boolean defaultRole;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
