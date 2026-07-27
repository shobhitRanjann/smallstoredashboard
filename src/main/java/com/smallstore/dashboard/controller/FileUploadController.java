package com.smallstore.dashboard.controller;

import com.smallstore.dashboard.dto.FileUploadResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.UUID;

@RestController
@RequestMapping("/files/api")
public class FileUploadController {
    private static final Path RESOURCE_UPLOAD_DIR = Paths.get("src", "main", "resources", "uploads");
    private static final Path CHUNK_UPLOAD_DIR = RESOURCE_UPLOAD_DIR.resolve(".chunks");

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFileChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadId") String uploadId,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("originalFilename") String originalFilename,
            @RequestParam("contentType") String contentType,
            @RequestParam("totalFileSize") long totalFileSize
    ) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please select a file to upload.");
        }

        if (!StringUtils.hasText(uploadId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload id is required.");
        }

        if (chunkIndex < 0 || totalChunks <= 0 || chunkIndex >= totalChunks) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid chunk metadata.");
        }

        String safeOriginalFilename = sanitizeOriginalFilename(originalFilename);
        String safeUploadId = uploadId.replaceAll("[^a-zA-Z0-9-_]", "");

        if (safeUploadId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload id is invalid.");
        }

        try {
            Files.createDirectories(RESOURCE_UPLOAD_DIR);
            Files.createDirectories(CHUNK_UPLOAD_DIR);

            Path uploadTempDir = CHUNK_UPLOAD_DIR.resolve(safeUploadId);
            Files.createDirectories(uploadTempDir);

            Path chunkPath = uploadTempDir.resolve(chunkIndex + ".part");
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, chunkPath, StandardCopyOption.REPLACE_EXISTING);
            }

            if (chunkIndex < totalChunks - 1) {
                return ResponseEntity.ok(new FileUploadResponse(
                        false,
                        chunkIndex,
                        totalChunks,
                        null,
                        safeOriginalFilename,
                        defaultContentType(contentType),
                        totalFileSize,
                        null,
                        "Chunk uploaded successfully."
                ));
            }

            validateAllChunksExist(uploadTempDir, totalChunks);

            String storedFilename = buildStoredFilename(safeOriginalFilename);
            Path destination = RESOURCE_UPLOAD_DIR.resolve(storedFilename).normalize();
            assembleChunks(uploadTempDir, destination, totalChunks);
            deleteDirectory(uploadTempDir);

            return ResponseEntity.ok(new FileUploadResponse(
                    true,
                    chunkIndex,
                    totalChunks,
                    storedFilename,
                    safeOriginalFilename,
                    defaultContentType(contentType),
                    totalFileSize,
                    destination.toAbsolutePath().toString(),
                    "File uploaded successfully."
            ));
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save uploaded file.", exception);
        }
    }

    private void validateAllChunksExist(Path uploadTempDir, int totalChunks) {
        for (int index = 0; index < totalChunks; index++) {
            if (!Files.exists(uploadTempDir.resolve(index + ".part"))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing chunk " + index + ".");
            }
        }
    }

    private void assembleChunks(Path uploadTempDir, Path destination, int totalChunks) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(
                destination,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        )) {
            for (int index = 0; index < totalChunks; index++) {
                Path chunkPath = uploadTempDir.resolve(index + ".part");
                Files.copy(chunkPath, outputStream);
            }
        }
    }

    private void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }

        try (var paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    private String sanitizeOriginalFilename(String originalFilename) {
        String cleaned = StringUtils.cleanPath(
                StringUtils.hasText(originalFilename) ? originalFilename : "upload.bin"
        );

        if (cleaned.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename is invalid.");
        }

        return cleaned;
    }

    private String defaultContentType(String contentType) {
        return StringUtils.hasText(contentType) ? contentType : "application/octet-stream";
    }

    private String buildStoredFilename(String originalFilename) {
        String extension = "";
        int extensionIndex = originalFilename.lastIndexOf('.');

        if (extensionIndex >= 0) {
            extension = originalFilename.substring(extensionIndex);
            originalFilename = originalFilename.substring(0, extensionIndex);
        }

        String sanitizedBaseName = originalFilename.replaceAll("[^a-zA-Z0-9-_]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (sanitizedBaseName.isBlank()) {
            sanitizedBaseName = "file";
        }

        return sanitizedBaseName + "-" + UUID.randomUUID() + extension;
    }
}
