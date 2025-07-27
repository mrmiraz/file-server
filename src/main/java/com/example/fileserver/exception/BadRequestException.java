package com.example.fileserver.exception;

public class BadRequestException extends CustomException {
    public BadRequestException(String description) {
            super(description);
    }
}
