package com.example.fileserver.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final String description;

    public CustomException(String description) {
        super(description);
        this.description = description;
    }
}
