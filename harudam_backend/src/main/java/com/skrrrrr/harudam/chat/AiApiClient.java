package com.skrrrrr.harudam.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AiApiClient {

    private final WebClient webClient = WebClient.create("http://localhost:8000"); // FastAPI 서버 주소

    /**
     * STT (음성 파일 → 텍스트 변환)
     */
    public String callStt(MultipartFile file) throws IOException {
        File convFile = convert(file);

        Map response = webClient.post()
                .uri("/stt")
                .body(BodyInserters.fromMultipartData("file", new FileSystemResource(convFile)))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response != null ? response.get("text").toString() : "STT 실패";
    }

    /**
     * OpenAI (텍스트 → AI 응답)
     */
    public String callOpenAi(String text) {
        Map response = webClient.post()
                .uri("/openai")
                .body(BodyInserters.fromFormData("message", text))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response != null ? response.get("response").toString() : "응답 실패";
    }

    /**
     * TTS (텍스트 → 자녀 목소리 음성 파일 생성)
     */
    public String callTts(String text, Long childId) {
        Map response = webClient.post()
                .uri("/tts")
                .body(BodyInserters.fromFormData("text", text)
                                   .with("child_id", childId.toString()))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response != null ? response.get("voiceUrl").toString() : null;
    }

    /**
     * MultipartFile → File 변환 (WebClient 업로드용)
     */
    private File convert(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("upload_", file.getOriginalFilename());
        file.transferTo(convFile);
        return convFile;
    }
}
