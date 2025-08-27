package com.skrrrrr.harudam.verification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class SmsTestController {

    private final SmsSender smsSender;

    @GetMapping("/sms")
    public ResponseEntity<String> testSms(@RequestParam String to,
                                          @RequestParam String msg) {
        String e164 = toE164Kr(to);
        smsSender.send(e164, msg);
        return ResponseEntity.ok("전송 요청 완료: " + e164);
    }

    /** 한국 번호 보정: "01012345678" → "+821012345678" */
    private static String toE164Kr(String raw) {
        if (raw == null) throw new IllegalArgumentException("to is null");
        String digits = raw.trim().replaceAll("\\D", ""); // 공백/구분자 제거

        if (digits.startsWith("0")) {                // 010xxxx → +82 10xxxx
            return "+82" + digits.substring(1);
        }
        if (digits.startsWith("82")) {              // 8210xxxx → +8210xxxx
            return "+" + digits;
        }
        if (raw.trim().startsWith("+")) {           // 이미 +8210xxxx
            return raw.trim();
        }
        // 다른 국가 포맷이면 여기서 예외 처리 or 로직 추가
        throw new IllegalArgumentException("한국 휴대폰 번호 형식이 아님: " + raw);
    }
}
