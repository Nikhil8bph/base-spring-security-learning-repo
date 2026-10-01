package com.example.sharedkernel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
    private T content;
    private Long totalElements;
    private Long totalPages;
    private Integer pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String sortDir;
}
