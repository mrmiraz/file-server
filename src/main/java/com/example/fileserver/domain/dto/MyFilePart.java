package com.example.fileserver.domain.dto;

import com.example.fileserver.util.validator.FieldValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyFilePart {
    private byte[] byteFile;
    private MultipartFile multipartFile;
    private String originalFilename;
    private String contentType;
    private String extension;
    private long size;
    private double sizeMb;

    public MyFilePart(byte[] bytes, String originalFilename) {
        this.byteFile = bytes;
        this.size = bytes.length;
        this.contentType = "text/plain";
        this.sizeMb =  (double) size / (1024 * 1024); // Convert bytes to MB
        setOriginalFileName(originalFilename);
    }

    public MyFilePart(byte[] bytes) {
        this(bytes, null);
    }

    public MyFilePart(MultipartFile multipartFile, String originalFilename) {
        this.multipartFile = multipartFile;
        this.size = multipartFile.getSize();
        this.contentType = multipartFile.getContentType();
        this.sizeMb =  (double) size / (1024 * 1024); // Convert bytes to MB
        setOriginalFileName(originalFilename);
    }

    public MyFilePart(MultipartFile multipartFile) {
        this(multipartFile, multipartFile.getOriginalFilename());
    }

    public void setOriginalFileName(String originalFilename){
        if (FieldValidator.isBlank(originalFilename)) {
            this.originalFilename = UUID.randomUUID().toString(); // Default filename if none provided
        }
        else {
            this.originalFilename = originalFilename;
        }
        setExtension(originalFilename);
    }

    public void setExtension(String originalFilename){
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i + 1);
        }
        this.extension = extension;
    }

    public boolean isEmpty() {
        return byteFile == null || byteFile.length == 0;
    }
}
