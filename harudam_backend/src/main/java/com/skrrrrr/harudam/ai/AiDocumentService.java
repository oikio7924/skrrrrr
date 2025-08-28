package com.skrrrrr.harudam.ai;

import java.nio.file.Path;
import java.util.List;

public interface AiDocumentService {
    Path generateAutobiography(Long parentId, List<String> diaryEntries, Path coverImage, Path parentVoiceModel);
}
