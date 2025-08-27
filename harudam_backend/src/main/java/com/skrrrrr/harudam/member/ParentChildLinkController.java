package com.skrrrrr.harudam.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 부모-자녀 관계 연결 API
 */
@RestController
@RequestMapping("/api/link")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ParentChildLinkController {

    private final ParentUserRepository parentUserRepository;
    private final ChildUserRepository childUserRepository;
    private final ParentChildLinkRepository parentChildLinkRepository;

    /**
     * 관계 생성 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class LinkRequest {
        private Long parentId;
        private Long childId;
    }

    /**
     * 관계 생성 응답 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class LinkResponse {
        private boolean success;
        private String message;
        private Long linkId;
    }

    /**
     * ✅ 부모-자녀 관계 생성 API
     */
    @PostMapping("/connect")
    public ResponseEntity<LinkResponse> connect(@RequestBody LinkRequest req) {
        if (req.getParentId() == null || req.getChildId() == null) {
            return ResponseEntity.badRequest()
                    .body(new LinkResponse(false, "parentId, childId는 필수입니다.", null));
        }

        ParentUser parent = parentUserRepository.findById(req.getParentId())
                .orElse(null);
        ChildUser child = childUserRepository.findById(req.getChildId())
                .orElse(null);

        if (parent == null || child == null) {
            return ResponseEntity.badRequest()
                    .body(new LinkResponse(false, "부모 또는 자녀를 찾을 수 없습니다.", null));
        }

        // 이미 존재하는 관계인지 확인
        boolean exists = parentChildLinkRepository
                .findAll()
                .stream()
                .anyMatch(link ->
                        link.getParentUser().getId().equals(req.getParentId()) &&
                        link.getChildUser().getId().equals(req.getChildId())
                );

        if (exists) {
            return ResponseEntity.ok(
                    new LinkResponse(true, "이미 부모-자녀 관계가 존재합니다.", null)
            );
        }

        // 새로운 관계 저장
        ParentChildLink link = new ParentChildLink();
        link.setParentUser(parent);
        link.setChildUser(child);
        ParentChildLink saved = parentChildLinkRepository.save(link);

        return ResponseEntity.ok(
                new LinkResponse(true, "부모-자녀 관계가 생성되었습니다.", saved.getLinkId())
        );
    }
}
