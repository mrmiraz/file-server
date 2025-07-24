package com.example.fileserver.controller;

import com.example.fileserver.domain.entity.FileMetadata;
import com.example.fileserver.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> upload(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("File upload request received: " + file.getOriginalFilename());
        return ResponseEntity.ok(fileService.storeFile(file));
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<List<FileMetadata>> uploadMultipleFile(@RequestParam("files") MultipartFile[] files) throws IOException {
        return ResponseEntity.ok(fileService.storeMultipleFile(files));
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        Resource file = fileService.loadFile(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) {
        try {
            Resource resource = fileService.loadFileAsResource(filename);
            String contentType = fileService.getContentType(resource);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> delete(@PathVariable String filename) {
        fileService.deleteFile(filename);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileMetadata>> list() {
        return ResponseEntity.ok(fileService.listAllFiles());
    }
}
