package com.kgu.studywithme.studynotice.presentation;

import com.kgu.studywithme.global.Authenticated;
import com.kgu.studywithme.global.resolver.Auth;
import com.kgu.studywithme.studynotice.application.usecase.DeleteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.UpdateStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.WriteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.presentation.dto.request.UpdateStudyNoticeCommentRequest;
import com.kgu.studywithme.studynotice.presentation.dto.request.WriteStudyNoticeCommentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4-7. 스터디 공지사항 댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices/{noticeId}/comments")
public class StudyNoticeCommentApiController {
    private final WriteStudyNoticeCommentUseCase writeStudyNoticeCommentUseCase;
    private final UpdateStudyNoticeCommentUseCase updateStudyNoticeCommentUseCase;
    private final DeleteStudyNoticeCommentUseCase deleteStudyNoticeCommentUseCase;

    @Operation(summary = "스터디 공지사항 댓글 작성 EndPoint")
    @PostMapping
    public ResponseEntity<Void> write(
            @Auth final Authenticated authenticated,
            @PathVariable final Long noticeId,
            @RequestBody @Valid final WriteStudyNoticeCommentRequest request
    ) {
        writeStudyNoticeCommentUseCase.invoke(new WriteStudyNoticeCommentCommand(noticeId, authenticated.id(), request.content()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 공지사항 댓글 수정 EndPoint")
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> update(
            @Auth final Authenticated authenticated,
            @PathVariable final Long noticeId,
            @PathVariable final Long commentId,
            @RequestBody @Valid final UpdateStudyNoticeCommentRequest request
    ) {
        updateStudyNoticeCommentUseCase.invoke(new UpdateStudyNoticeCommentCommand(commentId, authenticated.id(), request.content()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 공지사항 댓글 삭제 EndPoint")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @Auth final Authenticated authenticated,
            @PathVariable final Long noticeId,
            @PathVariable final Long commentId
    ) {
        deleteStudyNoticeCommentUseCase.invoke(new DeleteStudyNoticeCommentCommand(commentId, authenticated.id()));
        return ResponseEntity.noContent().build();
    }
}
