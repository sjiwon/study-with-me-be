package com.kgu.studywithme.studyreview.presentation;

import com.kgu.studywithme.auth.domain.model.Authenticated;
import com.kgu.studywithme.global.annotation.Auth;
import com.kgu.studywithme.studyreview.application.usecase.DeleteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.UpdateStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.WriteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.command.DeleteStudyReviewCommand;
import com.kgu.studywithme.studyreview.application.usecase.command.UpdateStudyReviewCommand;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewCommand;
import com.kgu.studywithme.studyreview.presentation.dto.request.UpdateStudyReviewRequest;
import com.kgu.studywithme.studyreview.presentation.dto.request.WriteStudyReviewRequest;
import com.kgu.studywithme.studyreview.presentation.dto.response.StudyReviewIdResponse;
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

@Tag(name = "4-9. 스터디 리뷰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/reviews")
public class StudyReviewApiController {
    private final WriteStudyReviewUseCase writeStudyReviewUseCase;
    private final UpdateStudyReviewUseCase updateStudyReviewUseCase;
    private final DeleteStudyReviewUseCase deleteStudyReviewUseCase;

    @Operation(summary = "스터디 리뷰 작성 EndPoint")
    @PostMapping
    public ResponseEntity<StudyReviewIdResponse> write(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId,
            @RequestBody @Valid final WriteStudyReviewRequest request
    ) {
        final Long reviewId = writeStudyReviewUseCase.invoke(new WriteStudyReviewCommand(studyId, authenticated.id(), request.content()));
        return ResponseEntity.ok(new StudyReviewIdResponse(reviewId));
    }

    @Operation(summary = "스터디 리뷰 수정 EndPoint")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<Void> update(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId,
            @PathVariable final Long reviewId,
            @RequestBody @Valid final UpdateStudyReviewRequest request
    ) {
        updateStudyReviewUseCase.invoke(new UpdateStudyReviewCommand(reviewId, authenticated.id(), request.content()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 리뷰 삭제 EndPoint")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId,
            @PathVariable final Long reviewId
    ) {
        deleteStudyReviewUseCase.invoke(new DeleteStudyReviewCommand(reviewId, authenticated.id()));
        return ResponseEntity.noContent().build();
    }
}
