package com.skrrrrr.harudam.member;

import com.skrrrrr.harudam.common.dto.ApiResponse;
import com.skrrrrr.harudam.common.enums.Gender;
import com.skrrrrr.harudam.common.enums.UserState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    // ---------------- DTO ----------------

    @Getter
    @NoArgsConstructor
    public static class ChildSignupRequest {
        private String userId;    // 소셜 로그인 ID
        private String password;  // 선택
        private String name;
        private String gender;    // "MALE" / "FEMALE"
        private String birth;     // yyyy-MM-dd
        private String phone;
        private String addr1;
        private String addr2;
        private String pictureUrl; // 원본 사진 URL
        private String voiceUrl;   // 원본 음성 URL
    }

    // ---------------- API ----------------

    /**
     * ✅ 자녀 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signup(@RequestBody ChildSignupRequest req) {
        if (req.getUserId() == null || req.getName() == null || req.getGender() == null
                || req.getBirth() == null || req.getPhone() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("INVALID_REQUEST"));
        }

        // ---------------- 중복 검증 ----------------
        if (childUserRepository.findByUserId(req.getUserId()).isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("DUPLICATE_USERID"));
        }
        if (childUserRepository.findByPhone(req.getPhone()).isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("DUPLICATE_PHONE"));
        }

        // ---------------- 신규 저장 ----------------
        ChildUser child = new ChildUser();
        child.setUserId(req.getUserId());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            child.setPassword(passwordEncoder.encode(req.getPassword())); // 암호화 저장
        }
        child.setName(req.getName());
        child.setGender(Gender.valueOf(req.getGender().toUpperCase()));
        child.setBirth(LocalDate.parse(req.getBirth()));
        child.setPhone(req.getPhone());
        child.setAddr1(req.getAddr1());
        child.setAddr2(req.getAddr2());
        child.setPictureUrl(req.getPictureUrl());
        child.setVoiceUrl(req.getVoiceUrl());
        child.setState(UserState.PENDING); // 기본값

        ChildUser saved = childUserRepository.save(child);

        return ResponseEntity.ok(ApiResponse.ok("SIGNUP_SUCCESS", saved.getId()));
    }
}
