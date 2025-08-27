package com.skrrrrr.harudam.verification;

import org.springframework.stereotype.Component;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TwilioSmsSender implements SmsSender {

    private final SmsProperties props;

    @Override
    public void send(String toE164, String message) {
        Twilio.init(props.getAccountSid(), props.getAuthToken());
        Message.creator(
                new PhoneNumber(toE164),
                new PhoneNumber(props.getFrom()),
                message
        ).create();
    }
}
