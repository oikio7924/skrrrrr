package com.skrrrrr.harudam.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * 파일 저장 서비스.
 * - child / parent 구분
 * - 각 id 별 picture / voice 디렉토리 생성
 * - 파일명 규칙 적용
 * - 부모 음성(chat1 ~ chat30) 관리
 * - AI 학습 결과 파일명 규칙 지원 (자녀/부모 모두)
 */
@Service
public class FileStorageService {

    private final FileStorageProperties properties;

    public FileStorageService(FileStorageProperties properties) {
        this.properties = properties;
    }

    // ------------------- 원본 파일 저장 -------------------

    /** 자녀 사진 저장 */
    public Path saveChildPicture(Long childId, MultipartFile file) throws IOException {
        Path pictureDir = getChildDir(childId).resolve("picture");
        Files.createDirectories(pictureDir);

        String filename = childId + "_picture_" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
        Path target = pictureDir.resolve(filename);
        file.transferTo(target);

        return target;
    }

    /** 자녀 목소리 저장 */
    public Path saveChildVoice(Long childId, MultipartFile file) throws IOException {
        Path voiceDir = getChildDir(childId).resolve("voice");
        Files.createDirectories(voiceDir);

        String filename = childId + "_voice_" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
        Path target = voiceDir.resolve(filename);
        file.transferTo(target);

        return target;
    }

    /** 부모 사진 저장 */
    public Path saveParentPicture(Long parentId, MultipartFile file) throws IOException {
        Path pictureDir = getParentDir(parentId).resolve("picture");
        Files.createDirectories(pictureDir);

        String filename = parentId + "_picture_" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
        Path target = pictureDir.resolve(filename);
        file.transferTo(target);

        return target;
    }

    /** 부모 목소리 저장 (chat1 ~ chat30) */
    public Path saveParentVoice(Long parentId, MultipartFile file, int chatNumber) throws IOException {
        if (chatNumber < 1 || chatNumber > 30) {
            throw new IllegalArgumentException("chat 번호는 1 ~ 30까지만 허용됩니다.");
        }

        Path voiceDir = getParentDir(parentId).resolve("voice");
        Files.createDirectories(voiceDir);

        String filename = parentId + "_chat" + chatNumber + ".mp3";
        Path target = voiceDir.resolve(filename);
        file.transferTo(target);

        return target;
    }

    // ------------------- AI 학습 결과 저장 -------------------

    /** 자녀 학습된 사진 저장 */
    public Path saveAiChildPicture(Long childId, byte[] data, String extension) throws IOException {
        Path pictureDir = getChildDir(childId).resolve("picture");
        Files.createDirectories(pictureDir);

        String filename = "ai_" + childId + "_picture_" + UUID.randomUUID() + extension;
        Path target = pictureDir.resolve(filename);
        Files.write(target, data);

        return target;
    }

    /** 자녀 학습된 목소리 저장 */
    public Path saveAiChildVoice(Long childId, byte[] data, String extension) throws IOException {
        Path voiceDir = getChildDir(childId).resolve("voice");
        Files.createDirectories(voiceDir);

        String filename = "ai_" + childId + "_voice_" + UUID.randomUUID() + extension;
        Path target = voiceDir.resolve(filename);
        Files.write(target, data);

        return target;
    }

    /** 부모 학습된 사진 저장 */
    public Path saveAiParentPicture(Long parentId, byte[] data, String extension) throws IOException {
        Path pictureDir = getParentDir(parentId).resolve("picture");
        Files.createDirectories(pictureDir);

        String filename = "ai_" + parentId + "_picture_" + UUID.randomUUID() + extension;
        Path target = pictureDir.resolve(filename);
        Files.write(target, data);

        return target;
    }

    /** 부모 학습된 목소리 저장 */
    public Path saveAiParentVoice(Long parentId, byte[] data, String extension) throws IOException {
        Path voiceDir = getParentDir(parentId).resolve("voice");
        Files.createDirectories(voiceDir);

        String filename = "ai_" + parentId + "_voice_" + UUID.randomUUID() + extension;
        Path target = voiceDir.resolve(filename);
        Files.write(target, data);

        return target;
    }

    // ---------------- 내부 헬퍼 메소드 ----------------

    private Path getChildDir(Long childId) {
        return properties.getUploadPath().resolve("child").resolve(childId.toString());
    }

    private Path getParentDir(Long parentId) {
        return properties.getUploadPath().resolve("parent").resolve(parentId.toString());
    }

    private String getExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf("."));
    }
}
