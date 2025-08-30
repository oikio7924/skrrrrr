package com.skrrrrr.harudam.autobiography;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutobiographyDto {
    private String parentName;     // 부모 이름
    private String title;          // 자서전 제목
    private List<ChapterDto> chapters; // 챕터 리스트
}
