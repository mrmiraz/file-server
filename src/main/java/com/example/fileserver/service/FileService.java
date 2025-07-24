package com.example.fileserver.service;
import com.example.fileserver.config.StorageProperties;
import com.example.fileserver.domain.entity.FileMetadata;
import com.example.fileserver.repository.FileMetadataRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    /*FileMetadata storeFile(MultipartFile file) throws IOException;
    Resource loadFile(String filename);
    void deleteFile(String filename);
    List<FileMetadata> listAllFiles();*/

    private final Path rootLocation;
    private final FileMetadataRepository repository;

    public FileService(StorageProperties properties, FileMetadataRepository repository) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.repository = repository;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

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

    public List<FileMetadata> storeMultipleFile(MultipartFile[] files) throws IOException {
        List<FileMetadata> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(storeFile(file));
        }
        return results;
    }

    public Resource loadFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            return new UrlResource(file.toUri());
        } catch (Exception e) {
            throw new RuntimeException("File not found: " + filename);
        }
    }

    public Resource loadFileAsResource(String filename) throws IOException {
        Path filePath = rootLocation.resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("File not found: " + filename);
        }
        return resource;
    }

    @Transactional
    public void deleteFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
            repository.deleteByStorageName(filename);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file", e);
        }
    }

    public List<FileMetadata> listAllFiles() {
        return repository.findAll();
    }

    public String getContentType(Resource resource) throws IOException {
        Path filePath = resource.getFile().toPath();
        String contentType = Files.probeContentType(filePath);
        return (contentType != null) ? contentType : "application/octet-stream";
    }
}