package com.alexdevstory.backend.exception;

public class BlogPostDeletionException extends RuntimeException {
    public BlogPostDeletionException(String message) {
        super(message);
    }

    public BlogPostDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
