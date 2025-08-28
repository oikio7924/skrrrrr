package com.skrrrrr.harudam.ai;

import org.springframework.stereotype.Service;

@Service
public class AiConversationServiceImpl implements AiConversationService {

    @Override
    public String generateTopic(Long childId, String conversationText) {
        // TODO: GPT-5 mini 로 화두 생성
        return "sample_topic";
    }

    @Override
    public String summarizeConversation(Long childId, String conversationText) {
        // TODO: GPT-5 mini 로 대화 요약
        return "summary_text";
    }

    @Override
    public String analyzeEmotion(Long childId, String conversationText) {
        // TODO: GPT-5 mini 로 감성 분석
        return "positive";
    }

    @Override
    public boolean detectRisk(Long childId, String conversationText) {
        // TODO: GPT-5 mini 로 위험 탐지 (예: 우울증 징후)
        return false;
    }

    @Override
    public String extractSchedule(Long childId, String conversationText) {
        // TODO: GPT-5 mini 로 일정 추출
        return "2025-09-01 학교 행사";
    }
}
