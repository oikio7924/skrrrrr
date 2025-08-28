package com.skrrrrr.harudam.file;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;
    private final CompressionService compressionService;

    public FileController(FileStorageService fileStorageService,
                          CompressionService compressionService) {
        this.fileStorageService = fileStorageService;
        this.compressionService = compressionService;
    }

    // -------------------- 자녀 --------------------

    @PostMapping("/child/{childId}/picture")
    public ResponseEntity<?> uploadChildPicture(
            @PathVariable Long childId,
            @RequestParam("file") MultipartFile file) throws IOException {
        Path saved = fileStorageService.saveChildPicture(childId, file);
        return ResponseEntity.ok(Map.of("path", saved.toString()));
    }

    @PostMapping("/child/{childId}/voice")
    public ResponseEntity<?> uploadChildVoice(
            @PathVariable Long childId,
            @RequestParam("file") MultipartFile file) throws IOException {
        Path saved = fileStorageService.saveChildVoice(childId, file);
        return ResponseEntity.ok(Map.of("path", saved.toString()));
    }

    // -------------------- 부모 --------------------

    @PostMapping("/parent/{parentId}/picture")
    public ResponseEntity<?> uploadParentPicture(
            @PathVariable Long parentId,
            @RequestParam("file") MultipartFile file) throws IOException {
        Path saved = fileStorageService.saveParentPicture(parentId, file);
        return ResponseEntity.ok(Map.of("path", saved.toString()));
    }

    @PostMapping("/parent/{parentId}/voice/{chatNumber}")
    public ResponseEntity<?> uploadParentVoice(
            @PathVariable Long parentId,
            @PathVariable int chatNumber,
            @RequestParam("file") MultipartFile file) throws IOException {
        Path saved = fileStorageService.saveParentVoice(parentId, file, chatNumber);
        return ResponseEntity.ok(Map.of("path", saved.toString()));
    }

    // -------------------- 부모 voice 압축 --------------------

    /** 부모 voice 폴더 압축 */
    @PostMapping("/parent/{parentId}/voice/compress")
    public ResponseEntity<?> compressParentVoice(@PathVariable Long parentId) throws IOException {
        Path zipPath = compressionService.compressAndCleanupParentVoice(parentId);
        return ResponseEntity.ok(Map.of(
                "message", "voice 폴더 압축 완료",
                "zipPath", zipPath.toString()
        ));
    }

    // -------------------- AI 학습 결과 --------------------

    @PostMapping("/child/{childId}/ai/picture")
    public ResponseEntity<?> saveAiChildPicture(
            @PathVariable Long childId,
            @RequestBody byte[] data) throws IOException {
        Path saved = fileStorageService.saveAiChildPicture(childId, data, ".jpg");
        return ResponseEntity.ok(Map.of("path", saved.toString()));
    }

    @PostMapping("/child/{childId}/ai/voice")
    public ResponseEntity<?> saveAiChildVoice(
            @PathVariable Long childId,
            @RequestBody byte[] data) throws IOException {
        Path saved = fileStorageService.saveAiChildVoice(childId, data, ".mp3");
        return ResponseEntity.ok(Map.of("path", saved.toString()));
    }

    @PostMapping("/parent/{parentId}/ai/picture")
    public ResponseEntity<?> saveAiParentPicture(
            @PathVariable Long parentId,
            @RequestBody byte[] data) throws IOException {
        Path saved = fileStorageService.saveAiParentPicture(parentId, data, ".jpg");
        return ResponseEntity.ok(Map.of("path", saved.toString()));
    }

    @PostMapping("/parent/{parentId}/ai/voice")
    public ResponseEntity<?> saveAiParentVoice(
            @PathVariable Long parentId,
            @RequestBody byte[] data) throws IOException {
        Path saved = fileStorageService.saveAiParentVoice(parentId, data, ".mp3");
        return ResponseEntity.ok(Map.of("path", saved.toString()));
    }

    // -------------------- 에러 처리 --------------------

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "파일 저장 실패", "message", e.getMessage()));
    }
}
