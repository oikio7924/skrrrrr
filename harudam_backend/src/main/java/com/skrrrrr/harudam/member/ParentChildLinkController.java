package com.skrrrrr.harudam.member;

import com.skrrrrr.harudam.ai.AiImageService;
import com.skrrrrr.harudam.ai.AiVoiceService;
import com.skrrrrr.harudam.common.dto.ApiResponse;
import com.skrrrrr.harudam.common.enums.UserState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.Map;

/**
 * 부모-자녀 관계 연결 + 서비스 시작하기 API
 */
@RestController
@RequestMapping("/api/link")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ParentChildLinkController {

    private final ParentUserRepository parentUserRepository;
    private final ChildUserRepository childUserRepository;
    private final ParentChildLinkRepository parentChildLinkRepository;
    private final AiImageService imageService;
    private final AiVoiceService voiceService;

    // ---------------- DTO ----------------

    @Getter
    @NoArgsConstructor
    public static class LinkRequest {
        private Long parentId;
        private Long childId;
    }

    // ---------------- API ----------------

    /**
     * ✅ 부모-자녀 관계 생성 API
     */
    @PostMapping("/connect")
    public ResponseEntity<ApiResponse<Long>> connect(@RequestBody LinkRequest req) {
        if (req.getParentId() == null || req.getChildId() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("INVALID_REQUEST"));
        }

        ParentUser parent = parentUserRepository.findById(req.getParentId()).orElse(null);
        ChildUser child = childUserRepository.findById(req.getChildId()).orElse(null);

        if (parent == null || child == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("USER_NOT_FOUND"));
        }

        // 이미 존재하는 관계인지 확인
        boolean exists = parentChildLinkRepository.findAll().stream()
                .anyMatch(link ->
                        link.getParentUser().getId().equals(req.getParentId()) &&
                        link.getChildUser().getId().equals(req.getChildId())
                );

        if (exists) {
            return ResponseEntity.ok(ApiResponse.ok("LINK_ALREADY_EXISTS", null));
        }

        // 새로운 관계 저장
        ParentChildLink link = new ParentChildLink();
        link.setParentUser(parent);
        link.setChildUser(child);
        ParentChildLink saved = parentChildLinkRepository.save(link);

        return ResponseEntity.ok(ApiResponse.ok("LINK_CREATED", saved.getLinkId()));
    }

    /**
     * ✅ 서비스 시작하기 버튼 → 부모/자녀 ACTIVE 전환 + AI 학습 실행
     */
    @PostMapping("/complete/{childId}/{parentId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> completeSignup(
            @PathVariable Long childId,
            @PathVariable Long parentId) {

        ChildUser child = childUserRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("CHILD_NOT_FOUND"));
        ParentUser parent = parentUserRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("PARENT_NOT_FOUND"));

        // ---------------- AI 학습 실행 (스텁) ----------------
        Path aiChildPic = imageService.generateDiaryImage(childId, "자녀 학습용 프로필 이미지");
        Path aiChildVoice = voiceService.trainChildVoice(childId,
                Path.of("uploads/child/" + childId + "/voice/sample.mp3"));
        Path aiParentPic = imageService.generateAutobiographyCover(parentId, "부모 자서전 표지 이미지");
        Path aiParentVoice = voiceService.trainParentVoice(parentId,
                Path.of("uploads/parent/" + parentId + "/" + parentId + "_voice.zip"));

        // ---------------- DB 업데이트 ----------------
        child.setAiPicturePath(aiChildPic.toString());
        child.setAiVoicePath(aiChildVoice.toString());
        parent.setAiPicturePath(aiParentPic.toString());
        parent.setAiVoicePath(aiParentVoice.toString());

        child.setState(UserState.ACTIVE);
        parent.setState(UserState.ACTIVE);

        childUserRepository.save(child);
        parentUserRepository.save(parent);

        return ResponseEntity.ok(ApiResponse.ok("SERVICE_STARTED", Map.of(
                "childId", child.getId().toString(),
                "parentId", parent.getId().toString(),
                "childAiPic", aiChildPic.toString(),
                "childAiVoice", aiChildVoice.toString(),
                "parentAiPic", aiParentPic.toString(),
                "parentAiVoice", aiParentVoice.toString()
        )));
    }
}
