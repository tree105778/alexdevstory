package com.alexdevstory.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlogPostFormDTO {
    private String title;
    private String content;
    private List<String> tags;
}
