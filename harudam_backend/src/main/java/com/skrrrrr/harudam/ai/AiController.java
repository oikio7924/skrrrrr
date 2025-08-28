package com.skrrrrr.harudam.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiConversationService conversationService;
    private final AiVoiceService voiceService;
    private final AiImageService imageService;
    private final AiDocumentService documentService;

    public AiController(AiConversationService conversationService,
                        AiVoiceService voiceService,
                        AiImageService imageService,
                        AiDocumentService documentService) {
        this.conversationService = conversationService;
        this.voiceService = voiceService;
        this.imageService = imageService;
        this.documentService = documentService;
    }

    // ---------------- 대화 ----------------
    @PostMapping("/conversation/{childId}/topic")
    public ResponseEntity<?> generateTopic(@PathVariable Long childId, @RequestBody String text) {
        return ResponseEntity.ok(Map.of("topic", conversationService.generateTopic(childId, text)));
    }

    @PostMapping("/conversation/{childId}/summary")
    public ResponseEntity<?> summarize(@PathVariable Long childId, @RequestBody String text) {
        return ResponseEntity.ok(Map.of("summary", conversationService.summarizeConversation(childId, text)));
    }

    @PostMapping("/conversation/{childId}/emotion")
    public ResponseEntity<?> analyzeEmotion(@PathVariable Long childId, @RequestBody String text) {
        return ResponseEntity.ok(Map.of("emotion", conversationService.analyzeEmotion(childId, text)));
    }

    @PostMapping("/conversation/{childId}/risk")
    public ResponseEntity<?> detectRisk(@PathVariable Long childId, @RequestBody String text) {
        return ResponseEntity.ok(Map.of("risk", conversationService.detectRisk(childId, text)));
    }

    @PostMapping("/conversation/{childId}/schedule")
    public ResponseEntity<?> extractSchedule(@PathVariable Long childId, @RequestBody String text) {
        return ResponseEntity.ok(Map.of("schedule", conversationService.extractSchedule(childId, text)));
    }

    // ---------------- 음성 ----------------
    @PostMapping("/voice/{userId}/stt")
    public ResponseEntity<?> speechToText(@PathVariable Long userId, @RequestBody Path file) {
        return ResponseEntity.ok(Map.of("text", voiceService.speechToText(file)));
    }

    @PostMapping("/voice/{userId}/tts")
    public ResponseEntity<?> textToSpeech(@PathVariable Long userId, @RequestBody String text) {
        return ResponseEntity.ok(Map.of("file", voiceService.textToSpeech(userId, text).toString()));
    }

    @PostMapping("/voice/parent/{parentId}/train")
    public ResponseEntity<?> trainParentVoice(@PathVariable Long parentId, @RequestBody Path voiceZip) {
        return ResponseEntity.ok(Map.of("model", voiceService.trainParentVoice(parentId, voiceZip).toString()));
    }

    @PostMapping("/voice/child/{childId}/train")
    public ResponseEntity<?> trainChildVoice(@PathVariable Long childId, @RequestBody Path sample) {
        return ResponseEntity.ok(Map.of("model", voiceService.trainChildVoice(childId, sample).toString()));
    }

    // ---------------- 이미지 ----------------
    @PostMapping("/image/child/{childId}/diary")
    public ResponseEntity<?> generateDiaryImage(@PathVariable Long childId, @RequestBody String prompt) {
        return ResponseEntity.ok(Map.of("file", imageService.generateDiaryImage(childId, prompt).toString()));
    }

    @PostMapping("/image/parent/{parentId}/cover")
    public ResponseEntity<?> generateAutobiographyCover(@PathVariable Long parentId, @RequestBody String prompt) {
        return ResponseEntity.ok(Map.of("file", imageService.generateAutobiographyCover(parentId, prompt).toString()));
    }

    // ---------------- 문서 ----------------
    @PostMapping("/document/parent/{parentId}/autobiography")
    public ResponseEntity<?> generateAutobiography(
            @PathVariable Long parentId,
            @RequestBody List<String> diaryEntries) {
        Path file = documentService.generateAutobiography(parentId, diaryEntries, null, null);
        return ResponseEntity.ok(Map.of("file", file.toString()));
    }
}
