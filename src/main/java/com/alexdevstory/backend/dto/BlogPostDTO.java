package com.alexdevstory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class BlogPostDTO {
    private String title;
    private String content;
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    @Builder.Default
    private List<BlogImageDTO> images = new ArrayList<>();
}
