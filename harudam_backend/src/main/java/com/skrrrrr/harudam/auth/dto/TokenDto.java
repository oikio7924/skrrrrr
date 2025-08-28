package com.skrrrrr.harudam.auth.dto;

import com.skrrrrr.harudam.common.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 응답 DTO
 * - 부모/자녀 모두 공용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserType userType; // ✅ 부모/자녀 구분
    private Object user;       // ChildUserDto or ParentLoginDto
}
