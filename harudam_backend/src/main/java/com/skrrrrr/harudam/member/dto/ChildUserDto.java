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
    public ChildUserDto(String valueOf, String name2, String phone2, Object object, String aiPicturePath2,
            String aiVoicePath2) {
        //TODO Auto-generated constructor stub
    }
    private Long id;
    private String userId;
    private String name;
    private Gender gender;
    private LocalDate birth;
    private String phone;
    private String addr1;
    private String addr2;
    private String pictureUrl;
    private String voiceUrl;
    private String aiPicturePath;
    private String aiVoicePath;
    private UserState state;
}
