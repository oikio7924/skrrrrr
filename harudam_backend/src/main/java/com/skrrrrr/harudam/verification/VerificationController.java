package com.skrrrrr.harudam.verification;

import com.skrrrrr.harudam.common.dto.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

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
    public static class CodeRes {
        private ZonedDateTime expiresAt; // ✅ 만료 시각
        private Integer ttlSeconds;      // ✅ TTL(초)
    }

    /* ===================== 자녀 인증 ===================== */
    @PostMapping("/send-child")
    public ResponseEntity<ApiResponse<CodeRes>> sendChildCode(@RequestBody SendChildCodeReq req) {
        if (req == null || req.getChildId() == null || req.getPhone() == null || req.getPhone().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("INVALID_REQUEST"));
        }

        VerificationService.SendCodeResult result = verificationService.sendChildCode(req.getChildId(), req.getPhone());
        return ResponseEntity.ok(
                ApiResponse.ok("CODE_SENT", new CodeRes(result.getExpiresAt(), result.getTtlSeconds()))
        );
    }

    @PostMapping("/verify-child")
    public ResponseEntity<ApiResponse<Object>> verifyChildCode(@RequestBody VerifyChildReq req) {
        if (req == null || req.getPhone() == null || req.getPhone().isBlank()
                || req.getCode() == null || req.getCode().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("INVALID_REQUEST"));
        }

        boolean ok = verificationService.verifyChildCode(req.getPhone(), req.getCode());
        if (ok) {
            return ResponseEntity.ok(ApiResponse.ok("VERIFICATION_SUCCESS", null));
        }
        return ResponseEntity.badRequest().body(ApiResponse.fail("VERIFICATION_FAILED"));
    }

    /* ===================== 부모 인증 ===================== */
    @PostMapping("/send-parent")
    public ResponseEntity<ApiResponse<CodeRes>> sendParentCode(@RequestBody SendParentCodeReq req) {
        if (req == null || req.getPhone() == null || req.getPhone().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("INVALID_REQUEST"));
        }

        VerificationService.SendCodeResult result = verificationService.sendParentCode(req.getPhone());
        return ResponseEntity.ok(
                ApiResponse.ok("CODE_SENT", new CodeRes(result.getExpiresAt(), result.getTtlSeconds()))
        );
    }

    @PostMapping("/verify-parent")
    public ResponseEntity<ApiResponse<Object>> verifyParentCode(@RequestBody VerifyParentCodeReq req) {
        if (req == null || req.getPhone() == null || req.getPhone().isBlank()
                || req.getCode() == null || req.getCode().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("INVALID_REQUEST"));
        }

        boolean ok = verificationService.verifyParentCode(req.getPhone(), req.getCode());
        if (ok) {
            return ResponseEntity.ok(ApiResponse.ok("VERIFICATION_SUCCESS", null));
        }
        return ResponseEntity.badRequest().body(ApiResponse.fail("VERIFICATION_FAILED"));
    }
}
