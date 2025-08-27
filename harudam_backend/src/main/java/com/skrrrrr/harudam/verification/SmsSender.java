package com.skrrrrr.harudam.verification;

public interface SmsSender {
	
    /**
     * @param toE164  수신자 번호 (예: +821012345678)
     * @param message 전송할 최종 메시지
     */
    void send(String toE164, String message);
}
