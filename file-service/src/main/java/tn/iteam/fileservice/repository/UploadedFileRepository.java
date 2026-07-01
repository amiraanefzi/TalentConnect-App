package tn.iteam.fileservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.iteam.fileservice.domain.UploadedFile;

import java.util.List;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, String> {

    List<UploadedFile> findByUploaderUserId(Long uploaderUserId);
}

