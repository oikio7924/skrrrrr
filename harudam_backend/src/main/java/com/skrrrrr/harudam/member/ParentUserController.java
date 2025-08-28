package com.skrrrrr.harudam.member;

import com.skrrrrr.harudam.common.enums.Gender;
import com.skrrrrr.harudam.common.enums.UserState;
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

    // ---------------- DTO ----------------

    @Getter
    @NoArgsConstructor
    public static class ParentSignupRequest {
        private String name;       // 부모 성함
        private String gender;     // "MALE"/"FEMALE"
        private String birth;      // yyyy-MM-dd
        private String phone;      // 인증된 휴대폰 번호
        private String addr1;      // 기본 주소
        private String addr2;      // 상세 주소
        private String pictureUrl; // 부모 사진 (파일 업로드 후 URL)
        private Long childId;      // 연결할 자녀 ID
    }

    @Getter
    @AllArgsConstructor
    public static class ParentSignupResponse {
        private boolean success;
        private String message;
        private Long parentId;
        private Long childId;
        private Long linkId;
    }

    // ---------------- API ----------------

    /**
     * ✅ 부모 회원가입 + 자녀와 자동 연결
     */
    @PostMapping("/signup")
    public ResponseEntity<ParentSignupResponse> signup(@RequestBody ParentSignupRequest req) {
        if (req.getName() == null || req.getName().isBlank()
                || req.getGender() == null || req.getGender().isBlank()
                || req.getBirth() == null || req.getBirth().isBlank()
                || req.getPhone() == null || req.getPhone().isBlank()
                || req.getChildId() == null) {
            return ResponseEntity.badRequest()
                    .body(new ParentSignupResponse(false, "필수값 누락", null, null, null));
        }

        // 휴대폰 번호 기준으로 부모 조회 (이미 있으면 업데이트, 없으면 새로 생성)
        ParentUser parent = parentUserRepository.findByPhone(req.getPhone())
                .orElseGet(ParentUser::new);

        parent.setName(req.getName());
        parent.setGender(Gender.valueOf(req.getGender().toUpperCase()));
        parent.setBirth(LocalDate.parse(req.getBirth()));
        parent.setPhone(req.getPhone());
        parent.setAddr1(req.getAddr1());
        parent.setAddr2(req.getAddr2());
        parent.setPictureUrl(req.getPictureUrl());
        parent.setState(UserState.PENDING);

        ParentUser savedParent = parentUserRepository.save(parent);

        // 자녀 조회
        ChildUser child = childUserRepository.findById(req.getChildId()).orElse(null);
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
