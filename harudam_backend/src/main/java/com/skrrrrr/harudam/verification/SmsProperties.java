package com.skrrrrr.harudam.verification;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "sms.twilio")
public class SmsProperties {
	
    // Twilio 계정 설정
    private String accountSid;
    private String authToken;
    private String from;

    // 메시지 템플릿: {code}, {ttlMinutes} 치환
    private String template = "[하루담] 인증번호 {code} (유효 {ttlMinutes}분)";
}
