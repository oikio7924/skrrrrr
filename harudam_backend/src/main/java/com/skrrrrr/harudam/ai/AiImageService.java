package com.skrrrrr.harudam.ai;

import java.nio.file.Path;

public interface AiImageService {
    Path generateDiaryImage(Long childId, String prompt); // 그림 일기 메인 이미지
    Path generateAutobiographyCover(Long parentId, String prompt); // 자서전 표지 이미지
}
