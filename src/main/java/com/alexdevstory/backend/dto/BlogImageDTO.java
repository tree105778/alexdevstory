package com.alexdevstory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BlogImageDTO {
    private byte[] imageData;
    private String fileName;
    private String contentType;
}
