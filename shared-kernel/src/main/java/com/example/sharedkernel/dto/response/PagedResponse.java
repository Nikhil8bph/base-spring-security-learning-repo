package com.example.sharedkernel.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(name = "PagedResponse", description = "Paginated result and page metadata")
public class PagedResponse<T> {
    @Schema(description = "Items on the current page")
    private T data;
    @Schema(description = "Total number of matching items", example = "125")
    private Long totalElements;
    @Schema(description = "Total number of pages", example = "7")
    private Long totalPages;
    @Schema(description = "Zero-based current page index", example = "0")
    private Integer pageNumber;
    @Schema(description = "Requested page size", example = "20")
    private Integer pageSize;
    @Schema(description = "Property used to sort", example = "id")
    private String sortBy;
    @Schema(description = "Sort direction", allowableValues = {"ASC", "DESC"}, example = "ASC")
    private String sortDir;
}
