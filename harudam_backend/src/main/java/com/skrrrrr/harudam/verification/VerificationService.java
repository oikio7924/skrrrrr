package com.skrrrrr.harudam.verification;

import java.security.SecureRandom;
import java.time.ZonedDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skrrrrr.harudam.auth.AuthCode;
import com.skrrrrr.harudam.auth.AuthCodeRepository;
import com.skrrrrr.harudam.common.enums.AuthCodePurpose;
import com.skrrrrr.harudam.common.enums.AuthCodeStatus;
import com.skrrrrr.harudam.common.enums.UserState;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ChildUserRepository;
import com.skrrrrr.harudam.member.ParentUser;
import com.skrrrrr.harudam.member.ParentUserRepository;
import com.skrrrrr.harudam.member.dto.ChildSignupReq;
import com.skrrrrr.harudam.member.dto.ParentSignupReq;

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

    /* ===================== 자녀 인증 ===================== */
    @Transactional
    public SendCodeResult sendChildCode(Long childId, String phone) {
        ChildUser child = childUserRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀를 찾을 수 없습니다. id=" + childId));

        String digits = normalizeDigits(phone);
        String toE164 = toE164Kr(digits);

        // 이전 미사용 코드 만료 처리
        authCodeRepository.findTopByTargetParentPhoneAndStatusOrderByCreatedAtDesc(digits, AuthCodeStatus.ISSUED)
                .ifPresent(c -> c.setStatus(AuthCodeStatus.EXPIRED));

        // 새 코드 생성
        String code = generate();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiresAt = now.plusSeconds(TTL_SECONDS);

        AuthCode ac = new AuthCode();
        ac.setCodeValue(code);
        ac.setIssuedByChild(child);
        ac.setTargetParentPhone(digits);
        ac.setStatus(AuthCodeStatus.ISSUED);
        ac.setPurpose(AuthCodePurpose.SIGNUP);
        ac.setExpiresAt(expiresAt);
        authCodeRepository.save(ac);

        // 문자 발송
        String msg = smsProperties.getTemplate()
                .replace("{code}", code)
                .replace("{ttlMinutes}", String.valueOf(TTL_SECONDS / 60));
        smsSender.send(toE164, msg);

        return new SendCodeResult(expiresAt, TTL_SECONDS);
    }

    @Transactional
    public boolean verifyChildCode(String phone, String code) {
        String digits = normalizeDigits(phone);

        AuthCode target = authCodeRepository
                .findTopByTargetParentPhoneAndStatusOrderByCreatedAtDesc(digits, AuthCodeStatus.ISSUED)
                .orElse(null);

        if (target == null) return false;
        if (ZonedDateTime.now().isAfter(target.getExpiresAt())) {
            target.setStatus(AuthCodeStatus.EXPIRED);
            return false;
        }
        if (!target.getCodeValue().equals(code)) return false;

        target.setStatus(AuthCodeStatus.USED);
        target.setUsedAt(ZonedDateTime.now());

        return true;
    }
    
    /* ===================== 자녀 정보 저장 ===================== */
    @Transactional
    public void finalizeChildSignup(ChildSignupReq req) {
        ChildUser child = childUserRepository.findById(req.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("자녀를 찾을 수 없습니다. id=" + req.getChildId()));

        child.setPhone(req.getPhone());
        child.setBirth(req.getBirth());
        child.setGender(req.getGender());
        child.setAddr1(req.getAddr1());
        child.setAddr2(req.getAddr2());
        child.setPictureUrl(req.getPictureUrl());
        child.setVoiceUrl(req.getVoiceUrl());
        child.setState(UserState.ACTIVE);

        childUserRepository.save(child);
    }

    /* ===================== 부모 인증 ===================== */
    @Transactional
    public SendCodeResult sendParentCode(String phone) {
        String digits = normalizeDigits(phone);
        String toE164 = toE164Kr(digits);

        authCodeRepository.findTopByTargetParentPhoneAndStatusOrderByCreatedAtDesc(digits, AuthCodeStatus.ISSUED)
            .ifPresent(c -> c.setStatus(AuthCodeStatus.EXPIRED));

        String code = generate();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiresAt = now.plusSeconds(TTL_SECONDS);

        AuthCode ac = new AuthCode();
        ac.setCodeValue(code);
        ac.setIssuedByChild(null);
        ac.setTargetParentPhone(digits);
        ac.setStatus(AuthCodeStatus.ISSUED);
        ac.setPurpose(AuthCodePurpose.SIGNUP);
        ac.setExpiresAt(expiresAt);
        authCodeRepository.save(ac);

        String msg = smsProperties.getTemplate()
                .replace("{code}", code)
                .replace("{ttlMinutes}", String.valueOf(TTL_SECONDS / 60));
        smsSender.send(toE164, msg);

        return new SendCodeResult(expiresAt, TTL_SECONDS);
    }

    @Transactional
    public boolean verifyParentCode(String phone, String code) {
        String digits = normalizeDigits(phone);

        AuthCode target = authCodeRepository
                .findTopByTargetParentPhoneAndStatusOrderByCreatedAtDesc(digits, AuthCodeStatus.ISSUED)
                .orElse(null);

        if (target == null) return false;
        if (ZonedDateTime.now().isAfter(target.getExpiresAt())) {
            target.setStatus(AuthCodeStatus.EXPIRED);
            return false;
        }
        if (!target.getCodeValue().equals(code)) return false;

        target.setStatus(AuthCodeStatus.USED);
        target.setUsedAt(ZonedDateTime.now());

        return true;
    }
    
    /* ===================== 부모 정보 저장 ===================== */
    @Transactional
    public ResponseEntity<?> finalizeParentSignup(ParentSignupReq req) {
        ParentUser parent = new ParentUser();
        parent.setName(req.getName());
        parent.setGender(req.getGender());
        parent.setBirth(req.getBirth());
        parent.setPhone(req.getPhone());
        parent.setAddr1(req.getAddr1());
        parent.setAddr2(req.getAddr2());
        parent.setPictureUrl(req.getPictureUrl());
        parent.setState(UserState.ACTIVE);

        parentUserRepository.save(parent);
        return ResponseEntity.ok("부모 회원가입 완료");
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
