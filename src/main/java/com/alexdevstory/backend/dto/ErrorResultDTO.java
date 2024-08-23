package com.alexdevstory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ErrorResultDTO {
    private String code;
    private String errorType;
    private String errorMessage;
}
