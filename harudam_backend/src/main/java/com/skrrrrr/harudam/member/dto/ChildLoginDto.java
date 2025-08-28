package com.skrrrrr.harudam.member.dto;

import com.skrrrrr.harudam.common.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 응답 전용 DTO
 * - 토큰 발급 후 사용자에게 필요한 최소 정보만 제공
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildLoginDto {
    private Long id;
    private String email;   // 카카오 계정 이메일 or userId
    private String name;    // 사용자 이름
    private UserType userType; // CHILD 고정
    private boolean needAdditionalInfo; // 추가정보 입력 필요 여부
}
