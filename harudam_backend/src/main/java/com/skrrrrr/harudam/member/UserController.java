package com.skrrrrr.harudam.member;

import com.skrrrrr.harudam.common.dto.ApiResponse;
import com.skrrrrr.harudam.file.FileStorageService;
import com.skrrrrr.harudam.member.dto.ChildUserDto;
import com.skrrrrr.harudam.member.dto.ParentUserDto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Users API
 * - GET   /api/users/me
 * - PUT   /api/users/me
 * - POST  /api/users/child/persona
 * - GET   /api/users/child/parent
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final ParentUserRepository parentRepo;
    private final ChildUserRepository childRepo;
    private final FileStorageService fileStorageService;
    private final ParentChildLinkRepository linkRepo;

    /** GET /api/users/me */
    @GetMapping("/me")
    public ApiResponse<?> me(@AuthenticationPrincipal User principal) {
        Long id = Long.valueOf(principal.getUsername());

        Optional<ParentUser> parentOpt = parentRepo.findById(id);
        if (parentOpt.isPresent()) {
            ParentUser p = parentOpt.get();
            ParentUserDto dto = new ParentUserDto(
                String.valueOf(p.getId()), p.getName(), p.getPhone(),
                p.getBirth() != null ? p.getBirth().toString() : null,
                p.getAddr1(), p.getAddr2(),
                p.getGender() != null ? p.getGender().name() : null,
                p.getPictureUrl()
            );
            return ApiResponse.ok("ME_PARENT", dto);
        }

        ChildUser c = childRepo.findById(id).orElseThrow();
        ChildUserDto dto = new ChildUserDto(
            String.valueOf(c.getId()), c.getName(), c.getPhone(),
            c.getBirth() != null ? c.getBirth().toString() : null,
            c.getAiPicturePath(), c.getAiVoicePath()
        );
        return ApiResponse.ok("ME_CHILD", dto);
    }

    /** PUT /api/users/me */
    @PutMapping("/me")
    public ApiResponse<?> updateMe(@AuthenticationPrincipal User principal,
                                   @RequestBody Map<String, String> body) {
        Long id = Long.valueOf(principal.getUsername());

        if (parentRepo.existsById(id)) {
            ParentUser p = parentRepo.findById(id).orElseThrow();
            if (body.containsKey("name"))  p.setName(body.get("name"));
            if (body.containsKey("phone")) p.setPhone(body.get("phone"));
            if (body.containsKey("birth")) p.setBirth(LocalDate.parse(body.get("birth")));
            if (body.containsKey("addr1")) p.setAddr1(body.get("addr1"));
            if (body.containsKey("addr2")) p.setAddr2(body.get("addr2"));
            parentRepo.save(p);
            return ApiResponse.ok("UPDATED", null);
        } else {
            ChildUser c = childRepo.findById(id).orElseThrow();
            if (body.containsKey("name"))  c.setName(body.get("name"));
            if (body.containsKey("phone")) c.setPhone(body.get("phone"));
            if (body.containsKey("birth")) c.setBirth(LocalDate.parse(body.get("birth")));
            childRepo.save(c);
            return ApiResponse.ok("UPDATED", null);
        }
    }

    /** POST /api/users/child/persona */
    @PostMapping("/child/persona")
    public ApiResponse<Map<String,String>> uploadPersona(
            @AuthenticationPrincipal User principal,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            @RequestParam(value = "voice", required = false) MultipartFile voice) throws Exception {

        Long childId = Long.valueOf(principal.getUsername());
        Map<String, String> result = new HashMap<>();

        if (picture != null && !picture.isEmpty()) {
            String path = fileStorageService.saveChildPicture(childId, picture).toString();
            result.put("picture", path);
            ChildUser c = childRepo.findById(childId).orElseThrow();
            c.setAiPicturePath(path);
            childRepo.save(c);
        }
        if (voice != null && !voice.isEmpty()) {
            String path = fileStorageService.saveChildVoice(childId, voice).toString();
            result.put("voice", path);
            ChildUser c = childRepo.findById(childId).orElseThrow();
            c.setAiVoicePath(path);
            childRepo.save(c);
        }

        return ApiResponse.ok("PERSONA_UPLOADED", result);
    }

    /** GET /api/users/child/parent */
    @GetMapping("/child/parent")
    public ApiResponse<ParentUserDto> myParent(@AuthenticationPrincipal User principal) {
        Long childId = Long.valueOf(principal.getUsername());

        // ParentChildLink 를 통해 부모 찾기
        ParentChildLink link = linkRepo.findFirstByChildUser_IdOrderByCreatedAtDesc(childId)
                .orElseThrow(() -> new RuntimeException("부모 연결 정보 없음"));

        ParentUser p = link.getParentUser();

        ParentUserDto dto = new ParentUserDto(
            String.valueOf(p.getId()), 
            p.getName(),
            p.getPhone(),
            p.getBirth() != null ? p.getBirth().toString() : null,
            p.getAddr1(),
            p.getAddr2(),
            p.getGender() != null ? p.getGender().name() : null,
            p.getPictureUrl()
        );
        return ApiResponse.ok("PARENT_INFO", dto);
    }
}
