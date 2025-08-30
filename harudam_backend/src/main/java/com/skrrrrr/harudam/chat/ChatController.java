package com.skrrrrr.harudam.chat;

import com.skrrrrr.harudam.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    private final ChatService chatService;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * ✅ 부모 음성 업로드 → AI 응답 반환
     */
    @PostMapping("/rooms/{childId}/messages")
    public ResponseEntity<ApiResponse<ChatMessage>> sendMessage(
            @PathVariable Long childId,
            @RequestParam("voiceFile") MultipartFile voiceFile,
            @AuthenticationPrincipal User principal) throws Exception {

        Long parentId = Long.valueOf(principal.getUsername()); // 로그인한 부모 ID
        ChatMessage botMessage = chatService.handleMessage(parentId, childId, voiceFile);

        return ResponseEntity.ok(ApiResponse.ok("AI_REPLY", botMessage));
    }

    /**
     * ✅ 하루치 대화 조회 (날짜별)
     */
    @GetMapping("/rooms/{childId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getMessagesByDate(
            @PathVariable Long childId,
            @RequestParam("date") String date,
            @AuthenticationPrincipal User principal) {

        Long parentId = Long.valueOf(principal.getUsername());
        LocalDate targetDate = LocalDate.parse(date);

        // 해당 날짜의 00:00 ~ 23:59:59 범위
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(LocalTime.MAX);

        List<ChatMessage> messages = chatMessageRepository
                .findByParentUser_IdAndChildUser_IdAndCreatedAtBetweenOrderByCreatedAtAsc(
                        parentId, childId, start, end);

        return ResponseEntity.ok(ApiResponse.ok("MESSAGES_BY_DATE", messages));
    }
}
