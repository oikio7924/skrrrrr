package com.skrrrrr.harudam.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 부모 로그인 응답 전용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParentLoginDto {
    private Long id;
    private String name;
    private String phone;
}
