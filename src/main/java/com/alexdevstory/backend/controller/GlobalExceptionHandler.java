package com.alexdevstory.backend.controller;

import com.alexdevstory.backend.dto.ErrorResultDTO;
import com.alexdevstory.backend.exception.BlogPostDeletionException;
import com.alexdevstory.backend.exception.BlogPostNotFoundException;
import com.alexdevstory.backend.exception.ImageProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BlogPostDeletionException.class)
    public ResponseEntity<ErrorResultDTO> handleBlogPostDeletionException(BlogPostDeletionException e) {
        log.error("BlogPostDeletion Error: {}", e.getMessage());
        ErrorResultDTO errorResult = ErrorResultDTO.builder()
                .code("BAD_REQUEST")
                .errorType("BlogPostDeletionError")
                .errorMessage(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    @ExceptionHandler(BlogPostNotFoundException.class)
    public ResponseEntity<ErrorResultDTO> handleBlogPostNotFoundException(BlogPostNotFoundException e) {
        log.error("BlogPostNotFound Error: {}", e.getMessage());
        ErrorResultDTO errorResult = ErrorResultDTO.builder()
                .code("NOT_FOUND")
                .errorType("BlogPostNotFoundError")
                .errorMessage(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);
    }

    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<ErrorResultDTO> handleImageProcessingException(ImageProcessingException e) {
        log.error("ImageProcessing Error: {}", e.getMessage());
        ErrorResultDTO errorResult = ErrorResultDTO.builder()
                .code("INTERNAL_SERVER_ERROR")
                .errorType("ImageProcessingError")
                .errorMessage(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred. Please try again later.");
    }
}
