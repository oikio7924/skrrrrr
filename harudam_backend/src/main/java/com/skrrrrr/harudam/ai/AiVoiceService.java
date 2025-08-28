package com.skrrrrr.harudam.ai;

import java.nio.file.Path;

public interface AiVoiceService {
    String speechToText(Path audioFile);              // STT
    Path textToSpeech(Long userId, String text);      // TTS
    Path trainParentVoice(Long parentId, Path voiceZip); // 부모 목소리 학습
    Path trainChildVoice(Long childId, Path voiceSample); // 자녀 목소리 학습
}
