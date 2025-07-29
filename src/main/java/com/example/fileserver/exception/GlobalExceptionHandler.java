package com.example.fileserver.exception;


import com.example.fileserver.domain.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends CustomException>, HttpStatus> EXCEPTION_STATUS_MAP = Map.of(
            com.example.fileserver.exception.BadRequestException.class, HttpStatus.BAD_REQUEST,
            com.example.fileserver.exception.UnauthorizedException.class, HttpStatus.UNAUTHORIZED,
            com.example.fileserver.exception.ResourceNotFoundException.class, HttpStatus.NOT_FOUND
    );

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(CustomException ex) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.BAD_REQUEST);
        ApiResponse<?> response = ApiResponse.error(ex.getDescription(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = ApiResponse.error("Something went wrong! Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        String message = String.format("Missing required request parameter: '%s'", ex.getParameterName());
        log.warn("Validation failed: {}", message);
        ApiResponse<?> response = ApiResponse.error(message, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingRequestBody(HttpMessageNotReadableException ex) {
        String message = "Required request body is missing or malformed.";
        log.warn("Request body error: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(message, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
