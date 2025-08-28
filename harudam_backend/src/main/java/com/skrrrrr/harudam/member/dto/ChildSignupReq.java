package com.skrrrrr.harudam.member.dto;

import java.time.LocalDate;
import com.skrrrrr.harudam.common.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildSignupReq {
    private Long childId;       // 소셜 로그인으로 미리 생성된 PK
    private String userId;      // 소셜 계정 ID (카카오/구글/네이버)
    private String password;    // 옵션
    private String name;
    private Gender gender;
    private LocalDate birth;
    private String phone;
    private String addr1;
    private String addr2;
    private String pictureUrl;
    private String voiceUrl;
}
