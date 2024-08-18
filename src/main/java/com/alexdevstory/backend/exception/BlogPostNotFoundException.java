package com.alexdevstory.backend.exception;

public class BlogPostNotFoundException extends RuntimeException {
    public BlogPostNotFoundException() {
        super();
    }

    public BlogPostNotFoundException(String message) {
        super(message);
    }
}
