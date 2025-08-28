package com.skrrrrr.harudam.verification;

import java.time.ZonedDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VerificationController {

    private final VerificationService verificationService;

    /* ===================== 요청 DTO ===================== */
    @Getter
    @NoArgsConstructor
    public static class SendChildCodeReq {
        private Long childId;
        private String phone;
    }

    @Getter
    @NoArgsConstructor
    public static class SendParentCodeReq {
        private String phone;
    }
    @Getter
    @NoArgsConstructor
    public static class VerifyChildReq {
        private String phone;
        private String code;
    }

    @Getter
    @NoArgsConstructor
    public static class VerifyParentCodeReq {
        private String phone;
        private String code;
    }

    /* ===================== 응답 DTO ===================== */
    @Getter
    @AllArgsConstructor
    public static class SimpleRes {
        private final boolean success;
        private final String message;
        private final ZonedDateTime expiresAt; // ✅ 만료 시각
        private final Integer ttlSeconds;      // ✅ TTL(초)
    }

    /* ===================== 자녀 인증 ===================== */
    @PostMapping("/send-child")
    public ResponseEntity<SimpleRes> sendChildCode(@RequestBody SendChildCodeReq req) {
        if (req == null || req.getChildId() == null || req.getPhone() == null || req.getPhone().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new SimpleRes(false, "childId, phone을 입력하세요.", null, null));
        }

        VerificationService.SendCodeResult result = verificationService.sendChildCode(req.getChildId(), req.getPhone());
        return ResponseEntity.ok(
                new SimpleRes(true, "자녀 인증번호를 전송했습니다.", result.getExpiresAt(), result.getTtlSeconds())
        );
    }

    @PostMapping("/verify-child")
    public ResponseEntity<SimpleRes> verifyChildCode(@RequestBody VerifyChildReq req) {
        if (req == null || req.getPhone() == null || req.getPhone().isBlank()
                || req.getCode() == null || req.getCode().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new SimpleRes(false, "phone, code를 입력하세요.", null, null));
        }

        boolean ok = verificationService.verifyChildCode(req.getPhone(), req.getCode());
        if (ok) {
            return ResponseEntity.ok(new SimpleRes(true, "자녀 인증 성공", null, null));
        }
        return ResponseEntity.badRequest().body(new SimpleRes(false, "자녀 인증 실패", null, null));
    }

    /* ===================== 부모 인증 ===================== */
    @PostMapping("/send-parent")
    public ResponseEntity<SimpleRes> sendParentCode(@RequestBody SendParentCodeReq req) {
        if (req == null || req.getPhone() == null || req.getPhone().isBlank()) {
            return ResponseEntity.badRequest()
                .body(new SimpleRes(false, "phone을 입력하세요.", null, null));
        }

        VerificationService.SendCodeResult result = verificationService.sendParentCode(req.getPhone());
        return ResponseEntity.ok(
            new SimpleRes(true, "부모님 동의 후 인증번호를 전송했습니다.", result.getExpiresAt(), result.getTtlSeconds())
        );
    }

    @PostMapping("/verify-parent")
    public ResponseEntity<SimpleRes> verifyParentCode(@RequestBody VerifyParentCodeReq req) {
        if (req == null || req.getPhone() == null || req.getPhone().isBlank()
                || req.getCode() == null || req.getCode().isBlank()) {
            return ResponseEntity.badRequest()
                .body(new SimpleRes(false, "phone, code를 입력하세요.", null, null));
        }

        boolean ok = verificationService.verifyParentCode(req.getPhone(), req.getCode());
        if (ok) {
            return ResponseEntity.ok(new SimpleRes(true, "인증 성공", null, null));
        }
        return ResponseEntity.badRequest().body(new SimpleRes(false, "인증 실패", null, null));
    }
}
