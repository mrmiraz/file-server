package com.example.fileserver.repository;

import com.example.fileserver.domain.entity.GenPrivateFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<GenPrivateFile, Long> {
    Optional<GenPrivateFile> findByWebPath(String uuid);
}