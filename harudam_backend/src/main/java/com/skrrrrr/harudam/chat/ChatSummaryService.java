package com.skrrrrr.harudam.chat;

import com.skrrrrr.harudam.common.enums.EmotionWord;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ChildUserRepository;
import com.skrrrrr.harudam.member.ParentUser;
import com.skrrrrr.harudam.member.ParentUserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatSummaryService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatDailySummaryRepository chatDailySummaryRepository;
    private final AiApiClient aiApiClient;
    private final ParentUserRepository parentUserRepository;
    private final ChildUserRepository childUserRepository;

    /**
     * 하루치 대화를 요약해서 ChatDailySummary에 저장
     */
    public ChatDailySummary summarizeDaily(Long parentId, Long childId, LocalDate targetDate) {
        // 1. 하루치 메시지 불러오기
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(LocalTime.MAX);

        List<ChatMessage> messages = chatMessageRepository
                .findByParentUser_IdAndChildUser_IdAndCreatedAtBetweenOrderByCreatedAtAsc(
                        parentId, childId, start, end);

        if (messages.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜에 대화가 없습니다.");
        }

        // 2. 전체 대화 텍스트 합치기
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : messages) {
            sb.append(msg.getSenderType()).append(": ").append(msg.getContent()).append("\n");
        }
        String conversation = sb.toString();

        // 3. AI 요약 생성
        String summaryText = aiApiClient.callOpenAi("다음 대화를 하루 일기로 3~4줄로 요약해줘:\n" + conversation);

        // 4. 감정 분석 (여기서는 단순히 키워드 매핑, 실제로는 별도 API 호출 가능)
        EmotionWord emotion = detectEmotion(conversation);

        // 5. 저장 (이미 있으면 업데이트)
        ParentUser parentRef = parentUserRepository.getReferenceById(parentId);
        ChildUser childRef = childUserRepository.getReferenceById(childId);

        ChatDailySummary summary = chatDailySummaryRepository
                .findByParentUser_IdAndChildUser_IdAndSummaryDate(parentId, childId, targetDate)
                .orElse(ChatDailySummary.builder()
                        .parentUser(parentRef) // 엔티티 참조 대신 Proxy만 생성
                        .childUser(childRef)
                        .summaryDate(targetDate)
                        .build());

        summary.setSummaryText(summaryText);
        summary.setEmotion(emotion);

        return chatDailySummaryRepository.save(summary);
    }

    /**
     * 간단 감정 감지 (실제는 AI로 대체 가능)
     */
    private EmotionWord detectEmotion(String text) {
        if (text.contains("행복") || text.contains("기뻐")) return EmotionWord.HAPPY;
        if (text.contains("슬퍼") || text.contains("우울")) return EmotionWord.SAD;
        if (text.contains("화나")) return EmotionWord.ANGRY;
        return EmotionWord.NEUTRAL;
    }
}
