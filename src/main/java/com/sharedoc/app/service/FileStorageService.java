package com.sharedoc.app.service;

import com.sharedoc.app.entity.FileAccess;
import com.sharedoc.app.entity.FileMetadata;
import com.sharedoc.app.entity.User;
import com.sharedoc.app.repository.FileAccessRepository;
import com.sharedoc.app.repository.FileMetadataRepository;
import com.sharedoc.app.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final Path uploadDir = Paths.get("uploads");

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private FileAccessRepository fileAccessRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadDir);
    }

    public FileMetadata storeFile(MultipartFile file, String userEmail) throws IOException {
        String filename = userEmail + "_" + file.getOriginalFilename();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        s3Service.uploadFile(file, filename);
        FileMetadata metadata = fileMetadataRepository.findByFilenameAndUploadedBy(file.getOriginalFilename(), user).orElse(new FileMetadata());
        metadata.setFilename(file.getOriginalFilename());
        metadata.setFilePath(filename);
        metadata.setContentType(file.getContentType());
        metadata.setSize(file.getSize());
        metadata.setUploadedBy(user);
        metadata.setUploadedAt(LocalDateTime.now());

        return fileMetadataRepository.save(metadata);
    }

    public UrlResource fetchFile(String filename, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Optional<FileAccess> fileAccess = fileAccessRepository.findByFilenameAndSecOwnerEmail(filename, email);
        if (fileAccess.isPresent()) {
            user = userRepository.findByEmail(fileAccess.get().getOwnerEmail()).orElseThrow();
        }
        FileMetadata fileMetadata = fileMetadataRepository.findByFilenameAndUploadedBy(filename, user).orElseThrow();
        try {
            return new UrlResource(s3Service.generatePresignedUrl(fileMetadata.getFilePath()));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid S3 URL", e);
        }
    }

    public String grantAccess(String filename, String ownerEmail, String secOwnerEmail) {
        if(fileAccessRepository.findByFilenameAndSecOwnerEmail(filename, secOwnerEmail).isPresent()) {
            return "Already have access";
        }
        User ownerUser = userRepository.findByEmail(ownerEmail).orElseThrow();
        userRepository.findByEmail(secOwnerEmail).orElseThrow(() -> new NoSuchElementException("User not found!"));

        fileMetadataRepository.findByFilenameAndUploadedBy(filename, ownerUser).orElseThrow(() -> new NoSuchElementException("File not found!"));

        FileAccess fileAccess = FileAccess.builder()
                .ownerEmail(ownerEmail)
                .secOwnerEmail(secOwnerEmail)
                .filename(filename)
                .createdAt(LocalDateTime.now())
                .build();

        fileAccessRepository.save(fileAccess);
        return "Access Granted!";
    }

    @Transactional
    public void revokeAccess(String filename, String ownerEmail, String secOwnerEmail) {
        fileAccessRepository.deleteByFilenameAndOwnerEmailAndSecOwnerEmail(filename, ownerEmail, secOwnerEmail);
    }

    @Transactional
    public void revokeAllAccess(String filename, String ownerEmail) {
        fileAccessRepository.deleteByFilenameAndOwnerEmail(filename, ownerEmail);
    }

    public List<String> getAllFiles(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<FileMetadata> fileMetadataList = fileMetadataRepository.findAllByUploadedBy(user);
        List<FileAccess> fileAccessList = fileAccessRepository.findAllBySecOwnerEmail(email);

        List<String> filesOwned = new ArrayList<>(fileMetadataList.stream().map(FileMetadata::getFilename).toList());
        filesOwned.addAll(fileAccessList.stream().map(FileAccess::getFilename).toList());

        return filesOwned;
    }
}
