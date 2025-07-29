package com.example.fileserver.util.type_detector;

import org.apache.tika.Tika;

import java.io.IOException;

public class TikaFileTypeDetector {

    private static final Tika tika = new Tika();

    public static String detectMimeType(byte[] data) {
        return tika.detect(data);
    }

    public static String detectMimeType(java.nio.file.Path path) {
        try {
            return tika.detect(path);
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}