package com.skrrrrr.harudam.chat;

import com.skrrrrr.harudam.file.FileStorageService;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ChildUserRepository;
import com.skrrrrr.harudam.member.ParentUser;
import com.skrrrrr.harudam.member.ParentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ParentUserRepository parentUserRepository;
    private final ChildUserRepository childUserRepository;
    private final FileStorageService fileStorageService;
    private final AiApiClient aiApiClient; // ✅ FastAPI 호출 담당

    /**
     * 부모 음성 메시지를 처리하고 AI 응답을 반환
     */
    public ChatMessage handleMessage(Long parentId, Long childId, MultipartFile voiceFile) throws Exception {
        ParentUser parent = parentUserRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("부모 없음"));
        ChildUser child = childUserRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 없음"));

        // 1. 부모 음성 파일 저장
        Path audioFilePath = fileStorageService.saveParentVoice(parentId, voiceFile, getNextChatNumber(parentId));

        // 2. 부모 음성 → STT
        String userText = aiApiClient.callStt(voiceFile);

        // 3. 텍스트 → OpenAI 응답
        String aiResponse = aiApiClient.callOpenAi(userText);

        // 4. 부모 메시지 저장
        ChatMessage userMessage = ChatMessage.builder()
                .parentUser(parent)
                .childUser(child)
                .senderType(ChatMessage.SenderType.USER)
                .content(userText)
                .voiceFilePath(audioFilePath.toString())
                .build();
        chatMessageRepository.save(userMessage);

        // 5. AI 응답 → 자녀 목소리로 TTS 생성
        String ttsFilePath = aiApiClient.callTts(aiResponse, childId);

        // 6. AI 응답 메시지 저장
        ChatMessage botMessage = ChatMessage.builder()
                .parentUser(parent)
                .childUser(child)
                .senderType(ChatMessage.SenderType.BOT)
                .content(aiResponse)
                .ttsFilePath(ttsFilePath)
                .build();
        chatMessageRepository.save(botMessage);

        // 7. 최종 응답은 AI 메시지
        return botMessage;
    }

    /**
     * 부모 목소리 저장 시 chatNumber(1~30) 순환 관리
     */
    private int getNextChatNumber(Long parentId) {
        // TODO: 실제 구현 시 DB 또는 파일 조회해서 최근 번호를 확인하고 다음 번호 리턴
        // 여기서는 임시로 랜덤 or 1~30 순환 로직
        return (int) (System.currentTimeMillis() % 30) + 1;
    }
}
