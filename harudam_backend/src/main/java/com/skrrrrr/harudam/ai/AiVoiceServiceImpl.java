package com.skrrrrr.harudam.ai;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class AiVoiceServiceImpl implements AiVoiceService {

    @Override
    public String speechToText(Path audioFile) {
        // TODO: GPT-4o mini STT
        return "나는 오늘 카레를 먹었어";
    }

    @Override
    public Path textToSpeech(Long userId, String text) {
        // TODO: GPT-4o mini TTS
        return Path.of("uploads/temp_tts.mp3");
    }

    @Override
    public Path trainParentVoice(Long parentId, Path voiceZip) {
        // TODO: 부모 목소리 학습 로직
        return Path.of("uploads/parent/" + parentId + "/ai_parent_voice_model.bin");
    }

    @Override
    public Path trainChildVoice(Long childId, Path voiceSample) {
        // TODO: 자녀 목소리 학습 로직
        return Path.of("uploads/child/" + childId + "/ai_child_voice_model.bin");
    }
}
