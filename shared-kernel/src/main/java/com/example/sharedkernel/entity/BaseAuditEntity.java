package com.example.sharedkernel.entity;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Calendar;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditEntity extends BaseEntity {

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Calendar createdDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private Calendar lastModifiedDate;

    private String deletedBy;

    private Calendar deletedDate;

    public void delete(String deletedBy) {
        super.delete();
        this.deletedBy = deletedBy;
        this.deletedDate = Calendar.getInstance();
    }
}
