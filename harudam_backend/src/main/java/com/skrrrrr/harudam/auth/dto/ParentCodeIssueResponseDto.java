package com.skrrrrr.harudam.auth.dto;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParentCodeIssueResponseDto {
	private String codeValue;
	private ZonedDateTime expiresAt;
}
