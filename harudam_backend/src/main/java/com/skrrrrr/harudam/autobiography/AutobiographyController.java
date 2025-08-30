package com.skrrrrr.harudam.autobiography;

import com.skrrrrr.harudam.common.dto.ApiResponse;
import com.skrrrrr.harudam.member.ParentUser;
import com.skrrrrr.harudam.member.ParentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autobiography")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AutobiographyController {

    private final AutobiographyService autobiographyService;
    private final ParentUserRepository parentUserRepository;

    /**
     * ✅ 자서전 조회 (자녀 전용)
     * GET /api/autobiography/{childId}
     */
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<AutobiographyDto>> getAutobiography(
            @PathVariable Long childId,
            @AuthenticationPrincipal User principal) {

        // 로그인한 부모 ID (JWT 기반)
        Long parentId = Long.valueOf(principal.getUsername());

        ParentUser parent = parentUserRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("부모 없음"));

        AutobiographyDto dto = autobiographyService.buildFromConversations(parent, childId);

        return ResponseEntity.ok(ApiResponse.ok("AUTOBIOGRAPHY", dto));
    }
}
