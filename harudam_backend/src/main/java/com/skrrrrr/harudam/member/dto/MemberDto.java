package com.skrrrrr.harudam.member.dto;

import com.skrrrrr.harudam.common.enums.UserType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
	private Long id;
	private String email;
	private String name;
	private UserType userType;
	private boolean needAdditionalInfo;
}
