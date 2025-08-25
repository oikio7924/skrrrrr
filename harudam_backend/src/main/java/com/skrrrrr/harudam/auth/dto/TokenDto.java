package com.skrrrrr.harudam.auth.dto;

import com.skrrrrr.harudam.member.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
	private String accessToken;
	private String refreshToken;
	private long expiresIn;
	private UserDto user;
}