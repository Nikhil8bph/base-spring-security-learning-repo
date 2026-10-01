package com.example.sharedkernel.dto;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.Calendar;

public class BaseAuditDTO {
    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Calendar createdDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @CreatedDate
    private Calendar lastModifiedDate;

    private String deletedBy;

    private Calendar deletedDate;
}
