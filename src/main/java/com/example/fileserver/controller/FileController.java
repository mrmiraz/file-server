package com.example.fileserver.controller;

import com.example.fileserver.domain.dto.ApiResponse;
import com.example.fileserver.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload-file")
    public ApiResponse<?> upload(@RequestParam(value = "file") MultipartFile file, @RequestParam("objectType") String objectType, @RequestParam(value = "objectId", required = false) Long objectId) throws IOException {
        return ApiResponse.success("Uploaded Successfully", fileService.storeRandomFile(file, objectType, objectId), HttpStatus.OK);
    }

    @PostMapping("/upload-bytes")
    public ApiResponse<?> uploadFileAsBytes(@RequestBody byte[] byteFile, @RequestParam("objectType") String objectType, @RequestParam(value = "objectId", required = false) Long objectId) throws IOException {
        return ApiResponse.success("Uploaded Successfully", fileService.storeRandomFile(byteFile, objectType, objectId), HttpStatus.OK);
    }

    @PostMapping("/upload-specific-file")
    public ApiResponse<?> upload(@RequestParam(value = "file") MultipartFile file,
                                 @RequestParam("directory") String directory,
                                 @RequestParam(value = "fileName", required = false) String fileName,
                                 @RequestParam("objectType") String objectType,
                                 @RequestParam(value = "objectId", required = false) Long objectId
                                 ) throws IOException {
        return ApiResponse.success("Uploaded Successfully", fileService.storeSpecificFile(file, directory, fileName, objectType, objectId), HttpStatus.OK);
    }

    @PostMapping("/upload-specific-bytes")
    public ApiResponse<?> uploadFileAsBytes(@RequestBody byte[] byteFile,
                                            @RequestParam("directory") String directory,
                                            @RequestParam(value = "fileName", required = false) String fileName,
                                            @RequestParam("objectType") String objectType,
                                            @RequestParam(value = "objectId", required = false) Long objectId
    ) throws IOException {
        return ApiResponse.success("Uploaded Successfully", fileService.storeSpecificFile(byteFile, directory, fileName, objectType, objectId), HttpStatus.OK);
    }

    @PostMapping("/upload-multiple")
    public ApiResponse<?> uploadMultipleFile(@RequestParam("files") MultipartFile[] files,  @RequestParam("objectType") String objectType, @RequestParam(value = "objectId", required = false) Long objectId) throws IOException {
        return ApiResponse.success("Uploaded Successfully", fileService.storeMultipleFile(files, objectType, objectId), HttpStatus.OK);
    }

    @GetMapping("/download/{filename}/{token}")
    public ResponseEntity<Resource> download(@PathVariable String filename, @PathVariable String token) {
        Resource file = fileService.loadFile(filename, token);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping("/view/{filename}/{token}")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename, @PathVariable String token) throws IOException {
        Resource resource = fileService.loadFileAsResource(filename, token);
        String contentType = fileService.getContentType(resource);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @DeleteMapping("/{filename}/{token}")
    public ResponseEntity<Void> delete(@PathVariable String filename, @PathVariable String token) {
        fileService.deleteFile(filename, token);
        return ResponseEntity.noContent().build();
    }

    /*@GetMapping("/list")
    public ResponseEntity<List<FileMetadata>> list() {
        return ResponseEntity.ok(fileService.listAllFiles());
    }*/

}
