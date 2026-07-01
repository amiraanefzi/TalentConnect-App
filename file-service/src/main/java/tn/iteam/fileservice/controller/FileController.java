package tn.iteam.fileservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.iteam.fileservice.domain.UploadedFile;
import tn.iteam.fileservice.repository.UploadedFileRepository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    @Value("${app.storage.location:./data/uploads}")
    private String storageLocation;

    @Value("${app.storage.max-size-mb:10}")
    private int maxSizeMb;

    private final UploadedFileRepository uploadedFileRepository;
    private Path storagePath;

    @PostConstruct
    public void init() throws IOException {
        storagePath = Paths.get(storageLocation).toAbsolutePath().normalize();
        Files.createDirectories(storagePath);
    }

    /** Upload d'un fichier CV — persisté en base de données. */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-Role", defaultValue = "EMPLOYEE") String role) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Fichier vide"));
        }
        long maxBytes = (long) maxSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Fichier trop volumineux (max " + maxSizeMb + " MB)"));
        }

        String fileId = UUID.randomUUID().toString();
        String originalName = Objects.requireNonNullElse(file.getOriginalFilename(), "fichier");
        Path destination = storagePath.resolve(fileId + "_" + originalName).normalize();
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        UploadedFile meta = UploadedFile.builder()
                .id(fileId)
                .originalName(originalName)
                .contentType(Objects.requireNonNullElse(file.getContentType(), "application/octet-stream"))
                .sizeBytes(file.getSize())
                .storagePath(destination.toString())
                .uploaderUserId(userId)
                .build();
        meta.prePersist();
        uploadedFileRepository.save(meta);   // ← persistance JPA réelle

        return ResponseEntity.ok(Map.of(
                "fileId", fileId,
                "fileName", originalName,
                "size", String.valueOf(file.getSize())
        ));
    }

    /** Vérifier existence d'un fichier — utilisé par candidatures-service. */
    @GetMapping("/{fileId}/metadata")
    public ResponseEntity<Map<String, Object>> metadata(
            @PathVariable String fileId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-Role", defaultValue = "EMPLOYEE") String role) {

        return uploadedFileRepository.findById(fileId)
                .map(meta -> ResponseEntity.ok(Map.<String, Object>of(
                        "fileId",      meta.getId(),
                        "fileName",    meta.getOriginalName(),
                        "contentType", meta.getContentType(),
                        "sizeBytes",   meta.getSizeBytes(),
                        "uploadedAt",  meta.getUploadedAt().toString()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Télécharger un fichier. */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> download(@PathVariable String fileId) throws MalformedURLException {
        Optional<UploadedFile> optional = uploadedFileRepository.findById(fileId);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        UploadedFile meta = optional.get();
        Resource resource = new UrlResource(Paths.get(meta.getStoragePath()).toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getOriginalName() + "\"")
                .body(resource);
    }

    /** Supprimer un fichier. */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(
            @PathVariable String fileId,
            @RequestHeader("X-User-Id") Long userId) throws IOException {

        Optional<UploadedFile> optional = uploadedFileRepository.findById(fileId);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        uploadedFileRepository.deleteById(fileId);
        Files.deleteIfExists(Paths.get(optional.get().getStoragePath()));
        return ResponseEntity.noContent().build();
    }

    /** Health check. */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "file-service"));
    }
}
