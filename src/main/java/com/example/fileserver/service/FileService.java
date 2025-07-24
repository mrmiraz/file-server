package com.example.fileserver.service;

import com.example.fileserver.config.StorageProperties;
import com.example.fileserver.domain.dto.FileMetadata;
import com.example.fileserver.domain.entity.GenPrivateFile;
import com.example.fileserver.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${comm.file.url}")
    private String FILE_SERVER_URL;// = "http://180.210.128.4:8081/admission-api/";

    public FileService(StorageProperties properties, FileMetadataRepository repository) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.repository = repository;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    //@Todo save with byte array
    //@Todo save with specific file name

    //directory mandatory, fileName null -> random uuid with extension
    public FileMetadata storeSpecificFile(MultipartFile uploadedFile,String directory, String fileName, String objectType, Long objectId) throws IOException {return  null;}

    public FileMetadata storeSpecificFile(byte[] fileData,String directory, String fileName, String objectType, Long objectId) throws IOException {return  null;}

    public FileMetadata storeRandomFile(byte[] fileData, String objectType, Long objectId) throws IOException {return  null;}

    //Store random file with UUID and extension
    public FileMetadata storeRandomFile(MultipartFile uploadedFile, String objectType, Long objectId) throws IOException {
        if (uploadedFile == null) {
            return null;
        }

        try {
            String extension = "";
            UUID uuid = UUID.randomUUID();
            String storageName;
            extension = "";
            int i = uploadedFile.getOriginalFilename().lastIndexOf('.');
            if (i > 0) {
                extension = uploadedFile.getOriginalFilename().substring(i + 1);
            }
            //@Todo if extension empty
            storageName = uuid + "." + extension;
            Path destinationFile = rootLocation.resolve(storageName);
            Files.copy(uploadedFile.getInputStream(), destinationFile);

            GenPrivateFile fileEntity = new GenPrivateFile();
            fileEntity.setDiskPath(storageName);
            fileEntity.setCreationDate(LocalDateTime.now());
            fileEntity.setFileExtension(extension);
            fileEntity.setIsDeleted(false);
            fileEntity.setMimeType(uploadedFile.getContentType());
            fileEntity.setFileName(uploadedFile.getOriginalFilename());
            fileEntity.setObjectType(objectType);
            fileEntity.setObjectId(objectId);
            fileEntity.setWebUuid(uuid); //web path hobe

            //@Todo: JWT token generation for file access
//            String jwt = jwtUtil.generateTokenForFiles(uuid.toString());
            String jwt = "";
            fileEntity.setFileSizeMb((double) uploadedFile.getSize() / 1000000.0);
            fileEntity = repository.save(fileEntity);

            FileMetadata fd = new FileMetadata();
            fd.setFileId(fileEntity.getId());
            fd.setCreationDate(fileEntity.getCreationDate());
            fd.setDownloadPath(FILE_SERVER_URL + "/files/download/"
                    + fileEntity.getWebUuid().toString() + "/" + jwt);
            fd.setFileExtension(fileEntity.getFileExtension());
            fd.setFileName(fileEntity.getFileName());
            fd.setFileSizeMb(fileEntity.getFileSizeMb());
            fd.setMimeType(fileEntity.getMimeType());
            fd.setObjectId(fileEntity.getObjectId());
            fd.setObjectType(fileEntity.getObjectType());
            fd.setThumbnailBas64(fileEntity.getThumbnailBas64());
            fd.setViewPath(FILE_SERVER_URL + "/files/view/"
                    + fileEntity.getWebUuid().toString() + "/" + jwt);
            fd.setCreatedBy(fileEntity.getCreatedBy());
            return fd;
        } catch (Exception ex) {
            System.out.println("exception occured ");
            ex.printStackTrace();
            return null;
        }
    }

    public List<FileMetadata> storeMultipleFile(MultipartFile[] files) throws IOException {
        List<FileMetadata> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(storeRandomFile(file, "defaultType", 0L));
        }
        return results;
    }

    public Resource loadFile(String baseFilename) {
        try {
            GenPrivateFile fileEntity = repository.findByWebUuid(UUID.fromString(baseFilename)).orElseThrow(() -> new FileNotFoundException("File not found."));
            Path file = rootLocation.resolve(fileEntity.getDiskPath());
            return new UrlResource(file.toUri());
        } catch (Exception e) {
            throw new RuntimeException("File not found.");
        }
    }

    public Resource loadFileAsResource(String baseFilename) throws IOException {
        GenPrivateFile fileEntity = repository.findByWebUuid(UUID.fromString(baseFilename)).orElseThrow(() -> new FileNotFoundException("File not found."));
        Path filePath = rootLocation.resolve(fileEntity.getDiskPath()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("File not found.");
        }
        return resource;
    }

    @Transactional
    public void deleteFile(String baseFilename) {
        try {
            GenPrivateFile fileEntity = repository.findByWebUuid(UUID.fromString(baseFilename)).orElseThrow(() -> new FileNotFoundException("File not found."));
            Path file = rootLocation.resolve(fileEntity.getDiskPath());
            Files.deleteIfExists(file);
            repository.delete(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file", e);
        }
    }

    /*public List<FileMetadata> listAllFiles() {
        return repository.findAll();
    }*/

    public String getContentType(Resource resource) throws IOException {
        Path filePath = resource.getFile().toPath();
        String contentType = Files.probeContentType(filePath);
        return (contentType != null) ? contentType : "application/octet-stream";
    }
}