package com.example.fileserver.domain.dto;

import com.example.fileserver.util.type_detector.TikaFileTypeDetector;
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
        this.contentType = TikaFileTypeDetector.detectMimeType(bytes);
        this.sizeMb =  (double) size / (1024 * 1024); // Convert bytes to MB
        setOriginalFileName(originalFilename);
    }

    public MyFilePart(byte[] bytes) {
        this(bytes, null);
    }

    public MyFilePart(MultipartFile multipartFile, String specificFileName) {
        this.multipartFile = multipartFile;
        this.size = multipartFile.getSize();
        this.contentType = multipartFile.getContentType();
        this.sizeMb =  (double) size / (1024 * 1024); // Convert bytes to MB
        setOriginalFileName(FieldValidator.isBlank(specificFileName)?
                multipartFile.getOriginalFilename() : specificFileName);
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
        if (originalFilename != null) {
            int i = originalFilename.lastIndexOf('.');
            if (i > 0 && i < originalFilename.length() - 1) {
                extension = originalFilename.substring(i + 1);
            }
        }
        else {
            extension = contentType.split("/")[1];
        }
        this.extension = extension;
    }

    public boolean isEmpty() {
        return byteFile == null || byteFile.length == 0;
    }
}
