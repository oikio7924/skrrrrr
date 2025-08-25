package com.skrrrrr.harudam.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentCodeIssueRequestDto {
	private Long childId;
	private String parentPhone;
}
