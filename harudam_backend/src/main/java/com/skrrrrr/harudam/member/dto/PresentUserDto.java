package com.skrrrrr.harudam.member.dto;

import java.time.LocalDate;
import com.skrrrrr.harudam.common.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 정보 조회 응답 DTO
 * (주소 합치기, pictureUrl만 내려주도록 단순화)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresentUserDto {
    private Long id;
    private String name;
    private Gender gender;
    private LocalDate birth;
    private String phone;
    private String pictureUrl; // ✅ 엔티티 기준
    private String addr1;
    private String addr2;
}
