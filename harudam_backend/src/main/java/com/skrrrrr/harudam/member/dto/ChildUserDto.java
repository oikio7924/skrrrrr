package com.skrrrrr.harudam.member.dto;

import java.time.LocalDate;
import com.skrrrrr.harudam.common.enums.Gender;
import com.skrrrrr.harudam.common.enums.UserState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildUserDto {
    private Long id;
    private String userId;
    private String name;
    private Gender gender;
    private LocalDate birth;
    private String phone;
    private String pictureUrl;
    private String voiceUrl;
    private UserState state;
}
