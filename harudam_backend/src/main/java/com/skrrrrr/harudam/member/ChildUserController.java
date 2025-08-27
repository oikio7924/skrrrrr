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
 * 자녀 회원가입 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/child")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ChildUserController {

    private final ChildUserRepository childUserRepository;

    /**
     * 자녀 회원가입 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class ChildSignupRequest {
        private String name;
        private String gender;   // "M" 또는 "F"
        private String birth;    // yyyy-MM-dd
        private String phone;
        private String address;
    }

    /**
     * 자녀 회원가입 응답 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class ChildSignupResponse {
        private boolean success;
        private String message;
        private Long childId;
    }

    /**
     * ✅ 자녀 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<ChildSignupResponse> signup(@RequestBody ChildSignupRequest req) {
        // 유효성 검사
        if (req.getName() == null || req.getName().isBlank()
                || req.getGender() == null || req.getGender().isBlank()
                || req.getBirth() == null || req.getBirth().isBlank()
                || req.getPhone() == null || req.getPhone().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ChildSignupResponse(false, "필수값 누락", null));
        }

        // ChildUser 엔티티 생성
        ChildUser child = new ChildUser();
        child.setName(req.getName());
        child.setGender(Gender.valueOf(req.getGender().toUpperCase())); // "M"/"F"
        child.setBirth(LocalDate.parse(req.getBirth()));                // yyyy-MM-dd
        child.setPhone(req.getPhone());
        child.setAddress(req.getAddress());

        // DB 저장
        ChildUser saved = childUserRepository.save(child);

        // 응답 반환
        return ResponseEntity.ok(
                new ChildSignupResponse(true, "자녀 정보가 저장되었습니다.", saved.getId())
        );
    }
}
