package com.sharedoc.app.controller;

import com.sharedoc.app.entity.FileMetadata;
import com.sharedoc.app.service.FileStorageService;
import com.sharedoc.app.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private S3Service s3Service;

    @Operation(summary = "Upload a file")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            FileMetadata saved = fileStorageService.storeFile(file, principal.getName());
            return ResponseEntity.ok("File uploaded: " + saved.getFilename());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed upload");
        }
    }

    @Operation(summary = "Download a file")
    @GetMapping(path = "/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename, Principal principal) {
        UrlResource urlResource = fileStorageService.fetchFile(filename, principal.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + urlResource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(urlResource);
    }

    @PostMapping("/grantAccess")
    public ResponseEntity<?> grantAccess(@RequestParam("filename") String filename, @RequestParam("secOwnerEmail") String secOwnerEmail, Principal principal) {
        String msg = fileStorageService.grantAccess(filename, principal.getName(), secOwnerEmail);
        return ResponseEntity.ok().body(msg);
    }

    @DeleteMapping("/revokeAccess")
    public ResponseEntity<?> revokeAccess(@RequestParam("filename") String filename, @RequestParam("secOwnerEmail") String secOwnerEmail, Principal principal) {
        fileStorageService.revokeAccess(filename, principal.getName(), secOwnerEmail);
        return ResponseEntity.ok().body(filename + " Access Revoked for: " + secOwnerEmail);
    }

    @DeleteMapping("/revokeAllAccess")
    public ResponseEntity<?> revokeAllAccess(@RequestParam("filename") String filename, Principal principal) {
        fileStorageService.revokeAllAccess(filename, principal.getName());
        return ResponseEntity.ok().body("All Access Revoked for file: " + filename);
    }

    @GetMapping("/viewAllFiles")
    public ResponseEntity<?> viewAllFiles(Principal principal) {
        return ResponseEntity.ok().body(fileStorageService.getAllFiles(principal.getName()));
    }
}
