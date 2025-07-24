package com.example.fileserver.service;

import com.example.fileserver.config.StorageProperties;
import com.example.fileserver.domain.entity.FileMetadata;
import com.example.fileserver.repository.FileMetadataRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl  {
/*
    private final Path rootLocation;
    private final FileMetadataRepository repository;

    public FileServiceImpl(StorageProperties properties, FileMetadataRepository repository) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.repository = repository;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public FileMetadata storeFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String storageName = UUID.randomUUID() + "-" + originalFilename;
        Path destinationFile = rootLocation.resolve(storageName);
        Files.copy(file.getInputStream(), destinationFile);

        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalName(originalFilename);
        metadata.setStorageName(storageName);
        metadata.setFileType(file.getContentType());
        metadata.setSize(file.getSize());
        metadata.setPath(destinationFile.toString());
        metadata.setUploadTime(LocalDateTime.now());

        return repository.save(metadata);
    }

    @Override
    public Resource loadFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            return new UrlResource(file.toUri());
        } catch (Exception e) {
            throw new RuntimeException("File not found: " + filename);
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
            repository.deleteByStorageName(filename);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file", e);
        }
    }

    @Override
    public List<FileMetadata> listAllFiles() {
        return repository.findAll();
    }*/
}
