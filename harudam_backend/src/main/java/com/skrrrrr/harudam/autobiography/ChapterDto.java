package com.skrrrrr.harudam.autobiography;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterDto {
    private Long id;          // 자동 부여 가능
    private String title;     // 챕터 제목
    private String content;   // 챕터 본문
    private String generatedImageUrl;
    private String voiceUrl;
}
