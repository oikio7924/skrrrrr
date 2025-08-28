package com.skrrrrr.harudam.member.dto;

import java.time.LocalDate;
import com.skrrrrr.harudam.common.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentSignupReq {
    private String name;
    private Gender gender;    // ENUM
    private LocalDate birth;
    private String phone;     // 인증된 휴대폰
    private String addr1;
    private String addr2;
    private String pictureUrl; // ✅ 엔티티와 통일
}
