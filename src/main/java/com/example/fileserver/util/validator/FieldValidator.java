package com.example.fileserver.util.validator;

public class FieldValidator {
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isBlank(Long value) {
        return value == null;
    }

    public static boolean isBlank(byte[] value) {
        return value == null || value.length == 0;
    }
}
