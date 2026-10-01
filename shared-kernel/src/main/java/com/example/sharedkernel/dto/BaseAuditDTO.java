package com.example.sharedkernel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseAuditDTO extends BaseDTO {
    private String createdBy;
    private Calendar createdDate;
    private String lastModifiedBy;
    private Calendar lastModifiedDate;
    private String deletedBy;
    private Calendar deletedDate;
}
