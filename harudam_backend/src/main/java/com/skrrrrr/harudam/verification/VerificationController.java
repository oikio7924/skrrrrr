package com.skrrrrr.harudam.verification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VerificationController {

    private final VerificationService verificationService;

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

    @Getter
    public static class SimpleRes {
        private final boolean success;
        private final String message;

        public SimpleRes(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<SimpleRes> sendVerificationCode(@RequestBody SendCodeReq req) {
        if (req == null || req.getChildId() == null || req.getPhone() == null || req.getPhone().isBlank()) {
            return ResponseEntity.badRequest().body(new SimpleRes(false, "childId, phone을 입력하세요."));
        }
        verificationService.sendCode(req.getChildId(), req.getPhone());
        return ResponseEntity.ok(new SimpleRes(true, "인증번호를 전송했습니다."));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<SimpleRes> verifyCode(@RequestBody VerifyCodeReq req) {
        if (req == null || req.getPhone() == null || req.getPhone().isBlank()
                || req.getCode() == null || req.getCode().isBlank()) {
            return ResponseEntity.badRequest().body(new SimpleRes(false, "phone, code를 입력하세요."));
        }
        boolean ok = verificationService.verifyCode(req.getPhone(), req.getCode());
        if (ok) {
            return ResponseEntity.ok(new SimpleRes(true, "인증 성공"));
        }
        return ResponseEntity.badRequest().body(new SimpleRes(false, "인증 실패"));
    }
}
