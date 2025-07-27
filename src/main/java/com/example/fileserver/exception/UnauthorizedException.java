package com.example.fileserver.exception;

public class UnauthorizedException  extends CustomException {
    public UnauthorizedException(String description) {
        super("Unauthorized: " + description);
    }
}
