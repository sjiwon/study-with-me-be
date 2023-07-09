package com.kgu.studywithme.study.controller.notice;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.study.application.notice.NoticeService;
import com.kgu.studywithme.study.controller.dto.request.NoticeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4-6. 스터디 공지사항 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyNoticeApiController {
    private final NoticeService noticeService;

    @Operation(summary = "스터디 공지사항 등록 EndPoint")
    @CheckStudyHost
    @PostMapping("/notice")
    public ResponseEntity<Void> register(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @RequestBody @Valid final NoticeRequest request
    ) {
        noticeService.register(studyId, request.title(), request.content());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 공지사항 삭제 EndPoint")
    @CheckStudyHost
    @DeleteMapping("/notices/{noticeId}")
    public ResponseEntity<Void> remove(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Long noticeId
    ) {
        noticeService.remove(noticeId, hostId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 공지사항 수정 EndPoint")
    @CheckStudyHost
    @PatchMapping("/notices/{noticeId}")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Long noticeId,
            @RequestBody @Valid final NoticeRequest request
    ) {
        noticeService.update(noticeId, hostId, request.title(), request.content());
        return ResponseEntity.noContent().build();
    }
}
