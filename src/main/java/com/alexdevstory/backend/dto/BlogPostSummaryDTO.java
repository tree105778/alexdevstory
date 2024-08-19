package com.alexdevstory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class BlogPostSummaryDTO {
    private String title;
    private String summary;
}
