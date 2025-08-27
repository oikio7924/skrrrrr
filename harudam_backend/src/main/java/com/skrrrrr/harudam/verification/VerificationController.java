package com.skrrrrr.harudam.verification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VerificationController {

    private final VerificationService verificationService;

    /* ===================== 요청 DTO ===================== */
    @Getter
    @NoArgsConstructor
    public static class SendCodeReq {
        private Long childId;
        private String phone;
    }

    @Getter
    @NoArgsConstructor
    public static class VerifyCodeReq {
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

    /** 기존: childId + phone 필요 */
    @PostMapping("/send-verification-code")
    public ResponseEntity<SimpleRes> sendVerificationCode(@RequestBody SendCodeReq req) {
        if (req == null || req.getChildId() == null || req.getPhone() == null || req.getPhone().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new SimpleRes(false, "childId, phone을 입력하세요.", null, null));
        }

        VerificationService.SendCodeResult result = verificationService.sendCode(req.getChildId(), req.getPhone());
        return ResponseEntity.ok(
                new SimpleRes(true, "인증번호를 전송했습니다.", result.getExpiresAt(), result.getTtlSeconds())
        );
    }

    /** ✅ 신규: childId 없이 phone만 필요 */
    @PostMapping("/send-verification-signup")
    public ResponseEntity<SimpleRes> sendVerificationCodeWithoutChild(@RequestBody VerifyCodeReq req) {
        if (req == null || req.getPhone() == null || req.getPhone().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new SimpleRes(false, "phone을 입력하세요.", null, null));
        }

        VerificationService.SendCodeResult result = verificationService.sendCodeWithoutChild(req.getPhone());
        return ResponseEntity.ok(
                new SimpleRes(true, "인증번호를 전송했습니다.", result.getExpiresAt(), result.getTtlSeconds())
        );
    }

    /** 인증번호 검증 */
    @PostMapping("/verify-code")
    public ResponseEntity<SimpleRes> verifyCode(@RequestBody VerifyCodeReq req) {
        if (req == null || req.getPhone() == null || req.getPhone().isBlank()
                || req.getCode() == null || req.getCode().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new SimpleRes(false, "phone, code를 입력하세요.", null, null));
        }

        boolean ok = verificationService.verifyCode(req.getPhone(), req.getCode());
        if (ok) {
            return ResponseEntity.ok(
                    new SimpleRes(true, "인증 성공", null, null)
            );
        }
        return ResponseEntity.badRequest()
                .body(new SimpleRes(false, "인증 실패", null, null));
    }
}
