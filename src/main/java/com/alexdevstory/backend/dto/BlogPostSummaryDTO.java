package com.alexdevstory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
@Builder
public class BlogPostSummaryDTO {
    private String title;
    private String summary;
}
