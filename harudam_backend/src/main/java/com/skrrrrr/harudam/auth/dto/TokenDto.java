
package com.skrrrrr.harudam.auth.dto;

import com.skrrrrr.harudam.member.dto.MemberDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
	private String accessToken;
	private String refreshToken;
	private long expiresIn;
	private MemberDto user;
}