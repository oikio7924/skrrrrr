package com.skrrrrr.harudam.auth.dto;

import com.skrrrrr.harudam.common.enums.SocialLogin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialLoginRequestDto {
	private String accessToken;
}