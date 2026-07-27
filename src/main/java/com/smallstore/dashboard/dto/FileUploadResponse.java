package com.smallstore.dashboard.dto;

public record FileUploadResponse(
        boolean completed,
        int chunkIndex,
        int totalChunks,
        String filename,
        String originalFilename,
        String contentType,
        long size,
        String savedPath,
        String message
) {
}
