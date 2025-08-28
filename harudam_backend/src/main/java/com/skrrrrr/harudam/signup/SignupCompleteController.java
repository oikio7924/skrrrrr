package com.skrrrrr.harudam.signup;

import com.skrrrrr.harudam.common.dto.ApiResponse;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ChildUserRepository;
import com.skrrrrr.harudam.member.ParentChildLink;
import com.skrrrrr.harudam.member.ParentChildLinkRepository;
import com.skrrrrr.harudam.member.ParentUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SignupCompleteController {

    private final ParentChildLinkRepository parentChildLinkRepository;
    private final ChildUserRepository childUserRepository;

    // ===== DTO =====
    @Getter
    @AllArgsConstructor
    public static class CompleteInfoResponse {
        private String parentName;  // 화면 노출용
        private String parentCode;  // 화면 노출/복사용
    }

    // ===== API =====

    /**
     * ✅ 회원가입 완료 정보 조회
     * - 현재 로그인한 Child(JWT subject)의 첫 번째 연결 Parent 정보를 반환
     */
    @GetMapping("/signup-complete-info")
    public ResponseEntity<ApiResponse<CompleteInfoResponse>> getSignupCompleteInfo() {
        Long childId = getCurrentChildId();
        if (childId == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("UNAUTHORIZED"));
        }

        // 자녀 존재 여부 확인
        Optional<ChildUser> childOpt = childUserRepository.findById(childId);
        if (childOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("CHILD_NOT_FOUND"));
        }

        // 부모-자녀 링크 중 첫 번째 항목 사용
        Optional<ParentChildLink> linkOpt = parentChildLinkRepository.findAll().stream()
                .filter(l -> l.getChildUser() != null && childId.equals(l.getChildUser().getId()))
                .findFirst();

        if (linkOpt.isEmpty() || linkOpt.get().getParentUser() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("PARENT_NOT_FOUND"));
        }

        ParentUser parent = linkOpt.get().getParentUser();
        String parentName = safe(parent.getName(), "보호자");
        String parentCode = buildParentCode(linkOpt.get(), parent);

        CompleteInfoResponse response = new CompleteInfoResponse(parentName, parentCode);

        return ResponseEntity.ok(ApiResponse.ok("COMPLETE_INFO_SUCCESS", response));
    }

    // ===== Helpers =====

    /**
     * JWT 필터에서 넣어둔 Authentication(User)의 username = childId 문자열
     */
    private Long getCurrentChildId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) return null;
        try {
            return Long.parseLong(user.getUsername());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }

    /**
     * 부모 코드 생성 로직(예시):
     * - 링크 ID를 base36으로 인코딩하여 사람이 읽기 쉬운 코드로 제공
     * - 예: linkId=123 -> "P-3F"
     */
    private String buildParentCode(ParentChildLink link, ParentUser parent) {
        long linkId = link.getLinkId() == null ? 0L : link.getLinkId();
        String base36 = Long.toString(linkId, 36).toUpperCase();
        return "P-" + base36;
    }
}
