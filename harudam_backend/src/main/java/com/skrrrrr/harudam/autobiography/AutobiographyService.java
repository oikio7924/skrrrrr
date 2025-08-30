package com.skrrrrr.harudam.autobiography;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skrrrrr.harudam.chat.AiApiClient;
import com.skrrrrr.harudam.chat.ChatDailySummary;
import com.skrrrrr.harudam.chat.ChatDailySummaryRepository;
import com.skrrrrr.harudam.chat.ChatMessage;
import com.skrrrrr.harudam.chat.ChatMessageRepository;
import com.skrrrrr.harudam.member.ParentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutobiographyService {

    private final ChatMessageRepository chatMessageRepository;
    private final AiApiClient aiApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AutobiographyDto buildFromConversations(ParentUser parent, Long childId) {
        // 1. 부모-자녀 전체 대화 원본 불러오기
    List<ChatMessage> messages =
        chatMessageRepository.findByParentUser_IdAndChildUser_IdOrderByCreatedAtAsc(
            parent.getId(), childId);

        if (messages.isEmpty()) {
            throw new IllegalArgumentException("대화 기록이 없습니다.");
        }

        // 2. 원본 대화 텍스트 합치기
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : messages) {
            sb.append("[").append(msg.getCreatedAt()).append("] ")
              .append(msg.getSenderType()).append(": ")
              .append(msg.getContent()).append("\n");
        }
        String conversation = sb.toString();

        // 3. AI API 호출 (챕터 JSON 형식 요청)
        String aiResponse = aiApiClient.callOpenAi(
            "다음 대화 기록을 기반으로 부모님의 삶을 회고록/자서전 형태로 정리해줘.\n" +
            "챕터 단위로 나누고 반드시 JSON 배열 형식으로 반환해.\n" +
            "JSON 예시:\n" +
            "[{\"title\": \"어린 시절 이야기\", \"content\": \"...\"}, {\"title\": \"청년기 이야기\", \"content\": \"...\"}]\n\n" +
            conversation
        );

        // 4. JSON → ChapterDto 리스트 변환
        List<ChapterDto> chapters;
        try {
            chapters = objectMapper.readValue(
                aiResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ChapterDto.class)
            );
        } catch (Exception e) {
            // JSON 파싱 실패 시 AI 응답 전체를 하나의 챕터로 처리
            chapters = List.of(ChapterDto.builder()
                    .id(1L)
                    .title("부모님의 삶 이야기")
                    .content(aiResponse)
                    .build());
        }

        // 5. 최종 자서전 DTO 반환
        return AutobiographyDto.builder()
                .parentName(parent.getName())
                .title(parent.getName() + "님의 자서전")
                .chapters(chapters)
                .build();
    }
}
