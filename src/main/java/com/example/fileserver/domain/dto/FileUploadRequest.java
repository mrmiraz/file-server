package com.example.fileserver.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadRequest {
    byte[] byteFile;
    String objectType;
    Long objectId;
    String directory;
    String fileName;
}
