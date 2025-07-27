package com.example.fileserver.exception;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String description) {
            super(description);
    }
}
