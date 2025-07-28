package com.example.fileserver.service;

import com.example.fileserver.config.StorageProperties;
import com.example.fileserver.domain.dto.ByteArrayMultipartFile;
import com.example.fileserver.domain.dto.FileMetadata;
import com.example.fileserver.domain.entity.GenPrivateFile;
import com.example.fileserver.exception.BadRequestException;
import com.example.fileserver.exception.UnauthorizedException;
import com.example.fileserver.repository.FileMetadataRepository;
import com.example.fileserver.util.JwtUtil;
import com.example.fileserver.util.validator.FieldValidator;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class FileService {
    /*FileMetadata storeFile(MultipartFile file) throws IOException;
    Resource loadFile(String filename);
    void deleteFile(String filename);
    List<FileMetadata> listAllFiles();*/

    private final Path rootLocation;
    private final FileMetadataRepository repository;
    private final JwtUtil jwtUtil;

    @Value("${comm.file.url}")
    private String FILE_SERVER_URL;// = "http://180.210.128.4:8081/admission-api/";
    private final String RANDOM_FILE_FOLDER = "random";
    private final String SPECIFIC_FILE_FOLDER = "specific";

    public FileService(StorageProperties properties, FileMetadataRepository repository, JwtUtil jwtUtil) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.repository = repository;
        this.jwtUtil = jwtUtil;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    //save with byte array
    //save with specific file name
    public FileMetadata storeSpecificFile(byte[] fileData, String directory, String fileName, String objectType, Long objectId) throws IOException {
        if (fileData == null) {
            throw new BadRequestException("Uploaded file has no content.");
        }

        if (FieldValidator.isBlank(directory)) {
            throw new BadRequestException("Directory is required.");
        }

        if (FieldValidator.isBlank(objectType)) {
            throw new BadRequestException("Object type is required.");
        }
        MultipartFile uploadedFile = byteArrayToMultipartFileExample(fileData, fileName);
        return  storeSpecificFile(uploadedFile, directory, fileName, objectType, objectId);
    }

    //directory mandatory, fileName null -> random uuid with extension
    public FileMetadata storeSpecificFile(MultipartFile uploadedFile, String directory, String fileName, String objectType, Long objectId) throws IOException {
        if (uploadedFile == null) {
            throw new BadRequestException("Uploaded file has no content.");
        }

        if (FieldValidator.isBlank(objectType)) {
            throw new BadRequestException("Object type is required.");
        }

        if (FieldValidator.isBlank(directory)) {
            throw new BadRequestException("Directory is required.");
        }

        try {
            String extension = "";
            extension = "";
            int i = uploadedFile.getOriginalFilename().lastIndexOf('.');
            if (i > 0) {
                extension = uploadedFile.getOriginalFilename().substring(i + 1);
            }
            //If extension empty
            if(FieldValidator.isBlank(extension)){
                extension = "txt";
            }
            UUID uuid = UUID.randomUUID();
            String storedFileName = uuid + "." + extension;

            //if the file name is not provided, use uuid with extension
            String originalFileName = FieldValidator.isBlank(fileName) ? storedFileName : fileName;

            //Upload file to disk
            Path relativeDiskPath = uploadFileToDisk(uploadedFile, storedFileName, directory);

            //Save file metadata to database
            return saveFileMetaToDatabase(uploadedFile, relativeDiskPath, uuid, extension, objectType, objectId, originalFileName);
        } catch (Exception ex) {
            System.out.println("exception occured ");
            ex.printStackTrace();
            return null;
        }
    }

    public FileMetadata storeRandomFile(byte[] fileData, String objectType, Long objectId) throws IOException {
        if (FieldValidator.isBlank(fileData)) {
            throw new BadRequestException("Uploaded file has no content.");
        }
        if (FieldValidator.isBlank(objectType)) {
            throw new BadRequestException("Object type is required.");
        }
        MultipartFile uploadedFile = byteArrayToMultipartFileExample(fileData, null);
        return  storeRandomFile(uploadedFile, objectType, objectId);
    }

    //Store random file with UUID and extension
    public FileMetadata storeRandomFile(MultipartFile uploadedFile, String objectType, Long objectId) throws IOException {
        if (uploadedFile == null) {
            throw new BadRequestException("Uploaded file has no content.");
        }

        if (FieldValidator.isBlank(objectType)) {
            throw new BadRequestException("Object type is required.");
        }

        try {
            String extension = "";
            extension = "";
            int i = uploadedFile.getOriginalFilename().lastIndexOf('.');
            if (i > 0) {
                extension = uploadedFile.getOriginalFilename().substring(i + 1);
            }
            //If extension empty
            if(FieldValidator.isBlank(extension)){
                extension = "txt";
            }
            UUID uuid = UUID.randomUUID();
            String storedFileName = uuid + "." + extension;
            Path relativeDiskPath = uploadFileToDisk(uploadedFile, storedFileName, "");
            return saveFileMetaToDatabase(uploadedFile, relativeDiskPath, uuid, extension, objectType, objectId, null);
        } catch (Exception ex) {
            System.out.println("exception occured ");
            ex.printStackTrace();
            return null;
        }
    }

    public Path uploadFileToDisk(MultipartFile uploadedFile, String storedFileName, String specifiedDirectory) throws IOException {
        if (uploadedFile == null) {
            throw new BadRequestException("Uploaded file has no content.");
        }
        // Get current date for folder structure
        Path destinationFolder, relativeDiskPath;
        //For specific file, use the specified directory
        if (!FieldValidator.isBlank(specifiedDirectory)) {
            destinationFolder = rootLocation.resolve(Paths.get(SPECIFIC_FILE_FOLDER, specifiedDirectory));
            relativeDiskPath = Paths.get(SPECIFIC_FILE_FOLDER, specifiedDirectory, storedFileName);
        }
        //For random file, use the specified directory
        else {
            LocalDate today = LocalDate.now();
            String monthFolder = today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + today.getYear();  // e.g., "January 2025"
            String dayFolder = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));  // e.g., "27-04-2025"
            destinationFolder = rootLocation.resolve(Paths.get(RANDOM_FILE_FOLDER, monthFolder, dayFolder));
            relativeDiskPath = Paths.get(RANDOM_FILE_FOLDER, monthFolder, dayFolder, storedFileName);
        }
        Files.createDirectories(destinationFolder);  // creates folder if not exist

        // Create the destination folder structure based on current date
        Path destinationFile = destinationFolder.resolve(storedFileName);
        // Save the file to the destination
        Files.copy(uploadedFile.getInputStream(), destinationFile);
        return relativeDiskPath;
    }

    private MultipartFile byteArrayToMultipartFileExample(byte[] fileData, String fileName){
        if (FieldValidator.isBlank(fileName)) {
            UUID uuid = UUID.randomUUID();
            fileName = uuid + "." + "txt"; // Default filename if none provided
        }
        try {
            String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
            String contentType = "text/plain";
            // Convert byte[] to MultipartFile
            return new ByteArrayMultipartFile(
                    fileData,
                    fileNameWithoutExtension,
                    fileName,
                    contentType
            );
        } catch (Exception e) {
            throw new BadRequestException("Invalid request.");
        }
    }

    private FileMetadata saveFileMetaToDatabase(MultipartFile uploadedFile, Path storedPath, UUID uuid, String extension, String objectType, Long objectId, String originalFileName) throws IOException {
        GenPrivateFile fileEntity = new GenPrivateFile();
        fileEntity.setDiskPath(storedPath.toString());
        fileEntity.setCreationDate(LocalDateTime.now());
        fileEntity.setFileExtension(extension);
        fileEntity.setIsDeleted(false);
        fileEntity.setMimeType(uploadedFile.getContentType());
        fileEntity.setFileName(FieldValidator.isBlank(originalFileName) ? uploadedFile.getOriginalFilename() : originalFileName);
        fileEntity.setObjectType(objectType);
        fileEntity.setObjectId(objectId);
        fileEntity.setWebPath(uuid.toString());

        //JWT token generation for file access
        String jwt = jwtUtil.generateAccessToken(uuid.toString());
        fileEntity.setFileSizeMb((double) uploadedFile.getSize() / 1000000.0);
        fileEntity = repository.save(fileEntity);

        FileMetadata fd = new FileMetadata();
        fd.setFileId(fileEntity.getId());
        fd.setCreationDate(fileEntity.getCreationDate());
        fd.setDownloadPath(FILE_SERVER_URL + "/files/download/"
                + fileEntity.getWebPath() + "/" + jwt);
        fd.setFileExtension(fileEntity.getFileExtension());
        fd.setFileName(fileEntity.getFileName());
        fd.setFileSizeMb(fileEntity.getFileSizeMb());
        fd.setMimeType(fileEntity.getMimeType());
        fd.setObjectId(fileEntity.getObjectId());
        fd.setObjectType(fileEntity.getObjectType());
        fd.setThumbnailBas64(fileEntity.getThumbnailBas64());
        fd.setViewPath(FILE_SERVER_URL + "/files/view/"
                + fileEntity.getWebPath() + "/" + jwt);
        fd.setCreatedBy(fileEntity.getCreatedBy());
        return fd;
    }

    public List<FileMetadata> storeMultipleFile(MultipartFile[] files, String objectType, Long objectId) throws IOException {
        List<FileMetadata> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(storeRandomFile(file, objectType, objectId));
        }
        return results;
    }

    public Resource loadFile(String baseFilename, String token) {
        try {
            validateToken(token);
            GenPrivateFile fileEntity = repository.findByWebPath(baseFilename).orElseThrow(() -> new FileNotFoundException("File not found."));
            Path file = rootLocation.resolve(fileEntity.getDiskPath());
            return new UrlResource(file.toUri());
        } catch (Exception e) {
            throw new RuntimeException("File not found.");
        }
    }

    public Resource loadFileAsResource(String baseFilename, String token) throws IOException {
        validateToken(token);
        GenPrivateFile fileEntity = repository.findByWebPath(baseFilename).orElseThrow(() -> new FileNotFoundException("File not found."));
        Path filePath = rootLocation.resolve(fileEntity.getDiskPath()).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("File not found.");
        }
        return resource;
    }

    public void validateToken(String token){
        // Validate the token if necessary
        if (!jwtUtil.validateJwtToken(token)){
            throw new UnauthorizedException("Unable to view.");
        }
    }

    @Transactional
    public void deleteFile(String baseFilename, String token) {
        try {
            validateToken(token);
            GenPrivateFile fileEntity = repository.findByWebPath(baseFilename).orElseThrow(() -> new FileNotFoundException("File not found."));
            Path file = rootLocation.resolve(fileEntity.getDiskPath());
            Files.deleteIfExists(file);
            repository.delete(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file");
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


    //@Todo url again web path
}