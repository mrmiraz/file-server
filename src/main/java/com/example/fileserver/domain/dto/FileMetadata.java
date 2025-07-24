package com.example.fileserver.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileMetadata {
    private Long fileId;
    private String objectType;
    private Long objectId;
    private String fileName;
    private String downloadPath;
    private String viewPath;
    private String mimeType;
    private String fileExtension;
    private Double fileSizeMb;
    private String thumbnailBas64;
    private LocalDateTime creationDate;
    private Long createdBy;
}

