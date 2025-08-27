package com.skrrrrr.harudam.verification;

import java.security.SecureRandom;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skrrrrr.harudam.auth.AuthCode;
import com.skrrrrr.harudam.auth.AuthCodeRepository;
import com.skrrrrr.harudam.common.enums.AuthCodeStatus;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ChildUserRepository;
import com.skrrrrr.harudam.member.ParentUser;
import com.skrrrrr.harudam.member.ParentUserRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationService {

    /** 인증코드 길이/TTL(초) */
    private static final int CODE_LENGTH = 6;
    private static final int TTL_SECONDS = 180; // 3분

    private static final SecureRandom RNG = new SecureRandom();

    private final AuthCodeRepository authCodeRepository;
    private final ChildUserRepository childUserRepository;
    private final ParentUserRepository parentUserRepository;
    private final SmsSender smsSender;
    private final SmsProperties smsProperties;

    /** 프론트에 만료정보를 내려주기 위한 반환 DTO */
    @Getter
    @AllArgsConstructor
    public static class SendCodeResult {
        private final ZonedDateTime expiresAt; // 서버 기준 만료 시각
        private final int ttlSeconds;          // 남은 TTL(초)
    }

    /** 기존: childId + phone 필요 (만료정보 반환) */
    @Transactional
    public SendCodeResult sendCode(Long childId, String parentPhone) {
        ChildUser child = childUserRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀를 찾을 수 없습니다. id=" + childId));

        String digits = normalizeDigits(parentPhone);
        String toE164 = toE164Kr(digits);

        // 이전 미사용 코드 만료 처리
        authCodeRepository.findTopByTargetParentPhoneAndStatusOrderByCreatedAtDesc(digits, AuthCodeStatus.ISSUED)
                .ifPresent(c -> c.setStatus(AuthCodeStatus.EXPIRED));

        // 새 코드 생성/저장
        String code = generate();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiresAt = now.plusSeconds(TTL_SECONDS);

        AuthCode ac = new AuthCode();
        ac.setCodeValue(code);
        ac.setIssuedByChild(child);
        ac.setTargetParentPhone(digits);
        ac.setStatus(AuthCodeStatus.ISSUED);
        ac.setExpiresAt(expiresAt);
        authCodeRepository.save(ac);

        // 문자 발송
        String msg = smsProperties.getTemplate()
                .replace("{code}", code)
                .replace("{ttlMinutes}", String.valueOf(TTL_SECONDS / 60));
        smsSender.send(toE164, msg);

        return new SendCodeResult(expiresAt, TTL_SECONDS);
    }

    /** ✅ 신규: childId 없이 phone만으로 코드 발송 (만료정보 반환) */
    @Transactional
    public SendCodeResult sendCodeWithoutChild(String parentPhone) {
        String digits = normalizeDigits(parentPhone);
        String toE164 = toE164Kr(digits);

        // 이전 미사용 코드 만료 처리
        authCodeRepository.findTopByTargetParentPhoneAndStatusOrderByCreatedAtDesc(digits, AuthCodeStatus.ISSUED)
                .ifPresent(c -> c.setStatus(AuthCodeStatus.EXPIRED));

        // 새 코드 생성/저장
        String code = generate();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiresAt = now.plusSeconds(TTL_SECONDS);

        AuthCode ac = new AuthCode();
        ac.setCodeValue(code);
        ac.setIssuedByChild(null); // child 없이 발급
        ac.setTargetParentPhone(digits);
        ac.setStatus(AuthCodeStatus.ISSUED);
        ac.setExpiresAt(expiresAt);
        authCodeRepository.save(ac);

        // 문자 발송
        String msg = smsProperties.getTemplate()
                .replace("{code}", code)
                .replace("{ttlMinutes}", String.valueOf(TTL_SECONDS / 60));
        smsSender.send(toE164, msg);

        return new SendCodeResult(expiresAt, TTL_SECONDS);
    }

    /** 입력한 인증코드 검증 */
    @Transactional
    public boolean verifyCode(String parentPhone, String code) {
        String digits = normalizeDigits(parentPhone);

        // 가장 최근 ISSUED 코드 조회
        AuthCode target = authCodeRepository
                .findTopByTargetParentPhoneAndStatusOrderByCreatedAtDesc(digits, AuthCodeStatus.ISSUED)
                .orElse(null);

        if (target == null) return false;

        // 만료 체크
        if (ZonedDateTime.now().isAfter(target.getExpiresAt())) {
            target.setStatus(AuthCodeStatus.EXPIRED);
            return false;
        }

        // 코드 일치 확인
        if (!target.getCodeValue().equals(code)) return false;

        // 사용 처리
        target.setStatus(AuthCodeStatus.USED);
        target.setUsedAt(ZonedDateTime.now());

        // 부모가 없으면 전화번호만으로 생성
        parentUserRepository.findByPhone(digits).orElseGet(() -> {
            ParentUser p = new ParentUser();
            p.setPhone(digits);
            return parentUserRepository.save(p);
        });

        return true;
    }

    /* ===================== 유틸 ===================== */

    private static String generate() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(RNG.nextInt(10));
        }
        return sb.toString();
    }

    private static String normalizeDigits(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("\\D", "");
    }

    /** 01012345678 -> +821012345678 */
    private static String toE164Kr(String digits) {
        if (digits.startsWith("0")) {
            return "+82" + digits.substring(1);
        }
        if (digits.startsWith("82")) {
            return "+" + digits;
        }
        return "+82" + digits;
    }
}
