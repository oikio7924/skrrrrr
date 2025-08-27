package com.skrrrrr.harudam.member;

import com.skrrrrr.harudam.common.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 부모 회원가입 / 정보 업데이트 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ParentUserController {

    private final ParentUserRepository parentUserRepository;
    private final ChildUserRepository childUserRepository;
    private final ParentChildLinkRepository parentChildLinkRepository;

    /**
     * 부모 회원가입/업데이트 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class ParentSignupRequest {
        private String name;
        private String gender;   // "M" 또는 "F"
        private String birth;    // yyyy-MM-dd
        private String phone;    // 인증된 번호
        private String address;
        private String picture;  // 사진 URL (옵션)
        private Long childId;    // ✅ 추가: 어떤 자녀와 연결할지
    }

    /**
     * 부모 회원가입/업데이트 응답 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class ParentSignupResponse {
        private boolean success;
        private String message;
        private Long parentId;
        private Long childId;
        private Long linkId;
    }

    /**
     * ✅ 부모 정보 저장/업데이트 + 자녀와 자동 연결
     */
    @PostMapping("/signup")
    public ResponseEntity<ParentSignupResponse> signup(@RequestBody ParentSignupRequest req) {
        // 유효성 검사
        if (req.getName() == null || req.getName().isBlank()
                || req.getGender() == null || req.getGender().isBlank()
                || req.getBirth() == null || req.getBirth().isBlank()
                || req.getPhone() == null || req.getPhone().isBlank()
                || req.getChildId() == null) {
            return ResponseEntity.badRequest()
                    .body(new ParentSignupResponse(false, "필수값 누락", null, null, null));
        }

        // phone 기준 부모 찾기 (있으면 업데이트, 없으면 생성)
        ParentUser parent = parentUserRepository.findByPhone(req.getPhone())
                .orElseGet(ParentUser::new);

        parent.setName(req.getName());
        parent.setGender(Gender.valueOf(req.getGender().toUpperCase()));
        parent.setBirth(LocalDate.parse(req.getBirth()));
        parent.setPhone(req.getPhone());
        parent.setAddress(req.getAddress());
        parent.setPicture(req.getPicture());

        ParentUser savedParent = parentUserRepository.save(parent);

        // 자녀 조회
        ChildUser child = childUserRepository.findById(req.getChildId())
                .orElse(null);
        if (child == null) {
            return ResponseEntity.badRequest()
                    .body(new ParentSignupResponse(false, "자녀를 찾을 수 없습니다.", savedParent.getId(), null, null));
        }

        // 부모-자녀 관계 자동 생성 (중복 체크 포함)
        ParentChildLink link = parentChildLinkRepository
                .findFirstByChildUser_IdOrderByCreatedAtDesc(req.getChildId())
                .orElseGet(() -> {
                    ParentChildLink newLink = new ParentChildLink();
                    newLink.setParentUser(savedParent);
                    newLink.setChildUser(child);
                    return parentChildLinkRepository.save(newLink);
                });

        return ResponseEntity.ok(
                new ParentSignupResponse(true, "부모 정보 및 관계가 저장되었습니다.",
                        savedParent.getId(), child.getId(), link.getLinkId())
        );
    }
}
