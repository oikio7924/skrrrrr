package com.skrrrrr.harudam.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skrrrrr.harudam.member.dto.ChildUpdateRequestDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Vue 서버 허용
public class MemberController {

	private final MemberService memberService;

	@PatchMapping("/child/{id}")
	public ResponseEntity<Void> updateChildInfo(
			@PathVariable Long id,
			@Valid @RequestBody ChildUpdateRequestDto requestDto){
		
		memberService.updateChildInfo(id, requestDto);
		return ResponseEntity.noContent().build();
	}

}
