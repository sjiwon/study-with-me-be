package com.kgu.studywithme.study.controller.notice;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.study.application.notice.NoticeCommentService;
import com.kgu.studywithme.study.controller.dto.request.NoticeCommentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4-7. 스터디 공지사항 댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices/{noticeId}")
public class StudyNoticeCommentApiController {
    private final NoticeCommentService noticeCommentService;

    @Operation(summary = "스터디 공지사항 댓글 등록 EndPoint")
    @PostMapping("/comment")
    public ResponseEntity<Void> register(
            @ExtractPayload final Long memberId,
            @PathVariable final Long noticeId,
            @RequestBody @Valid final NoticeCommentRequest request
    ) {
        noticeCommentService.register(noticeId, memberId, request.content());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 공지사항 댓글 삭제 EndPoint")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> remove(
            @ExtractPayload final Long memberId,
            @PathVariable final Long noticeId,
            @PathVariable final Long commentId
    ) {
        noticeCommentService.remove(commentId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 공지사항 댓글 수정 EndPoint")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long memberId,
            @PathVariable final Long noticeId,
            @PathVariable final Long commentId,
            @RequestBody @Valid final NoticeCommentRequest request
    ) {
        noticeCommentService.update(commentId, memberId, request.content());
        return ResponseEntity.noContent().build();
    }
}
