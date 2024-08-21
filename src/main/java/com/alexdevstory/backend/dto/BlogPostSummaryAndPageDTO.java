package com.alexdevstory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class BlogPostSummaryAndPageDTO {
    private Integer totalPage;
    private Long totalElements;
    private Integer currentPage;
    private Integer numberOfCurrentElements;

    private List<BlogPostSummaryDTO> summaries;
}
