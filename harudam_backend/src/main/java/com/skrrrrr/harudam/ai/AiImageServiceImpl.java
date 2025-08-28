package com.skrrrrr.harudam.ai;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class AiImageServiceImpl implements AiImageService {

    @Override
    public Path generateDiaryImage(Long childId, String prompt) {
        // TODO: GPT Image 1 API 호출
        return Path.of("uploads/child/" + childId + "/ai_diary_image.png");
    }

    @Override
    public Path generateAutobiographyCover(Long parentId, String prompt) {
        // TODO: GPT Image 1 API 호출
        return Path.of("uploads/parent/" + parentId + "/ai_autobiography_cover.png");
    }
}
