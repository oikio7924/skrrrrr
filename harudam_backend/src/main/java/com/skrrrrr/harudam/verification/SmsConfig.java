package com.skrrrrr.harudam.verification;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfig {
}
