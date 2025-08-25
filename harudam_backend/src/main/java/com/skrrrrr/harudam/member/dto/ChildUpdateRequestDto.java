package com.skrrrrr.harudam.member.dto;

import java.time.LocalDate;

import com.skrrrrr.harudam.common.enums.Gender;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildUpdateRequestDto {
	private String name;
	private Gender gender;
	private LocalDate birth;
	private String phone;
	private String address;
}
