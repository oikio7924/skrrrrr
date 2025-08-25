package com.skrrrrr.harudam.member;

import org.springframework.stereotype.Service;

import com.skrrrrr.harudam.member.dto.ChildUpdateRequestDto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final ChildUserRepository childUserRepository;

	// 자녀 선택 정보 업데이트 : 요청 Dto에서 null이 아닌 필드만 반영
	public void updateChildInfo(Long childId, ChildUpdateRequestDto dto) {
		ChildUser child = childUserRepository.findById(childId)
				.orElseThrow(() -> new EntityNotFoundException("ChildUser not found: id=" + childId));
		
		if(dto.getName() != null)		child.setName(dto.getName());
		if(dto.getGender() != null)		child.setGender(dto.getGender());
		if(dto.getBirth() != null)		child.setBirth(dto.getBirth());
		if(dto.getAddress() != null)	child.setAddress(dto.getAddress());
		
		if(dto.getPhone() != null && !dto.getPhone().equals(child.getPhone())) {
			// 전화번호 중복 체크
			if (childUserRepository.existsByPhone(dto.getPhone())) {
				throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.: " + dto.getPhone());
			}
			child.setPhone(dto.getPhone());
		}
	}
}
