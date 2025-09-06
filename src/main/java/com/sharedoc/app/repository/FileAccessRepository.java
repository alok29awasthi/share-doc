package com.sharedoc.app.repository;

import com.sharedoc.app.entity.FileAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileAccessRepository extends JpaRepository<FileAccess, Long> {
    Optional<FileAccess> findByFilenameAndSecOwnerEmail(String filename, String secOwnerEmail);
    List<FileAccess> findAllBySecOwnerEmail(String secOwnerEmail);
    void deleteByFilenameAndOwnerEmailAndSecOwnerEmail(String filename, String ownerEmail, String secOwnerEmail);
    void deleteByFilenameAndOwnerEmail(String filename, String ownerEmail);
}
