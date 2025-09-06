package com.sharedoc.app.repository;

import com.sharedoc.app.entity.FileMetadata;
import com.sharedoc.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findAllByUploadedBy(User user);
    Optional<FileMetadata> findByFilenameAndUploadedBy(String filename, User user);
    Optional<FileMetadata> findByFilePathAndUploadedBy(String filepath, User user);
}
