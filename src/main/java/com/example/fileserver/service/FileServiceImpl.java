package com.example.fileserver.service;

import org.springframework.stereotype.Service;

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
