package com.example.fileserver.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success; //true/false
    private int status;
    private String message;
    private T data;
    private LinkedHashMap<String, String> errors;
    private String debugHint;
    private Long timestamp;

    public static <T> ApiResponse<T> success(String message, T data, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status.value())
                .message(Optional.ofNullable(message).orElse(""))
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .message(Optional.ofNullable(message).orElse(""))
                .timestamp(System.currentTimeMillis())
                .build();
    }
}