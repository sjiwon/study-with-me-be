package com.kgu.studywithme.studynotice.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.presentation.dto.request.UpdateStudyNoticeCommentRequest;
import com.kgu.studywithme.studynotice.presentation.dto.request.WriteStudyNoticeCommentRequest;
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
    private final WriteStudyNoticeCommentUseCase writeStudyNoticeCommentUseCase;
    private final UpdateStudyNoticeCommentUseCase updateStudyNoticeCommentUseCase;
    private final DeleteStudyNoticeCommentUseCase deleteStudyNoticeCommentUseCase;

    @Operation(summary = "스터디 공지사항 댓글 작성 EndPoint")
    @PostMapping("/comment")
    public ResponseEntity<Void> write(
            @ExtractPayload final Long writerId,
            @PathVariable final Long noticeId,
            @RequestBody @Valid final WriteStudyNoticeCommentRequest request
    ) {
        writeStudyNoticeCommentUseCase.writeNoticeComment(
                new WriteStudyNoticeCommentUseCase.Command(
                        noticeId,
                        writerId,
                        request.content()
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 공지사항 댓글 수정 EndPoint")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long memberId,
            @PathVariable final Long noticeId,
            @PathVariable final Long commentId,
            @RequestBody @Valid final UpdateStudyNoticeCommentRequest request
    ) {
        updateStudyNoticeCommentUseCase.updateNoticeComment(
                new UpdateStudyNoticeCommentUseCase.Command(
                        commentId,
                        memberId,
                        request.content()
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 공지사항 댓글 삭제 EndPoint")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @ExtractPayload final Long memberId,
            @PathVariable final Long noticeId,
            @PathVariable final Long commentId
    ) {
        deleteStudyNoticeCommentUseCase.deleteNoticeComment(
                new DeleteStudyNoticeCommentUseCase.Command(
                        commentId,
                        memberId
                )
        );
        return ResponseEntity.noContent().build();
    }
}
