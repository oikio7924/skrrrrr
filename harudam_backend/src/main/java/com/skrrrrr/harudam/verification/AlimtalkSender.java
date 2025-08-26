package com.skrrrrr.harudam.verification;

public interface AlimtalkSender {
	void sendVerificationCode(String phone, String code, int ttlseconds);
}
