package com.skrrrrr.harudam.ai;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class AiDocumentServiceImpl implements AiDocumentService {

    @Override
    public Path generateAutobiography(Long parentId, List<String> diaryEntries, Path coverImage, Path parentVoiceModel) {
        // TODO: PDF 생성 로직
        // 1. diaryEntries 20개 이상 → 본문
        // 2. coverImage → 표지
        // 3. parentVoiceModel → 오디오북 생성 가능 (옵션)
        return Path.of("uploads/parent/" + parentId + "/ai_autobiography.pdf");
    }
}
