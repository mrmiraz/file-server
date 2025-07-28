package com.example.fileserver.util;

import jakarta.servlet.http.HttpServletRequest;

public class Utility {
    public static String parseToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
