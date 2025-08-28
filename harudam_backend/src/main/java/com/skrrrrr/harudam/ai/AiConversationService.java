package com.skrrrrr.harudam.ai;

public interface AiConversationService {
    String generateTopic(Long childId, String conversationText); // 화두 생성
    String summarizeConversation(Long childId, String conversationText); // 대화 요약
    String analyzeEmotion(Long childId, String conversationText); // 감성 분석
    boolean detectRisk(Long childId, String conversationText); // 위험 탐지
    String extractSchedule(Long childId, String conversationText); // 일정 추출
}
