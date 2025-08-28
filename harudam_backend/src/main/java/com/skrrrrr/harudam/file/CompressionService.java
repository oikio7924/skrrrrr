package com.skrrrrr.harudam.file;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 부모의 voice 폴더 압축 & 정리 서비스
 * - parent/{parentId}/voice 폴더만 압축
 * - 압축 후 voice 폴더 삭제
 */
@Service
public class CompressionService {

    private final FileStorageProperties properties;

    public CompressionService(FileStorageProperties properties) {
        this.properties = properties;
    }

    /**
     * 특정 parentId 의 voice 폴더를 압축 후 삭제
     * @param parentId 부모 ID
     * @return 생성된 zip 파일 경로
     */
    public Path compressAndCleanupParentVoice(Long parentId) throws IOException {
        Path voiceDir = properties.getUploadPath()
                .resolve("parent")
                .resolve(parentId.toString())
                .resolve("voice");

        if (!Files.exists(voiceDir)) {
            throw new FileNotFoundException("voice 폴더가 존재하지 않습니다: " + voiceDir);
        }

        // 압축 파일 경로: parent/{parentId}_voice.zip
        Path parentDir = voiceDir.getParent(); // parent/{parentId}
        Path zipPath = parentDir.resolve(parentId + "_voice.zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(voiceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(voiceDir.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }

        // voice 폴더 삭제
        deleteDirectoryRecursively(voiceDir);

        return zipPath;
    }

    // ---------------- 내부 유틸 ----------------

    private void deleteDirectoryRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) return;

        Files.walk(dir)
                .sorted((a, b) -> b.compareTo(a)) // 파일 → 디렉토리 순으로
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
}
