package com.skrrrrr.harudam.file;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 파일 저장소의 베이스 디렉터리를 주입받는 설정 클래스.
 * application.properties 의 `file.upload-dir` 값을 사용한다.
 *
 * 예) file.upload-dir=C:/desktop/project/skrrrrr/uploads
 */
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    /**
     * 설정에서 주입받는 원본 문자열 경로.
     */
    private String uploadDir;

    /**
     * 정규화된 절대경로(Path)로 반환.
     * 값이 비어있으면 프로젝트 루트 기준 ./uploads 를 기본값으로 사용.
     */
    public Path getUploadPath() {
        if (uploadDir == null || uploadDir.isBlank()) {
            return Paths.get("uploads").toAbsolutePath().normalize();
        }
        // ~ 치환 및 절대 경로 정규화
        String expanded = uploadDir.replace("~", System.getProperty("user.home"));
        return Paths.get(expanded).toAbsolutePath().normalize();
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
