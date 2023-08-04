package com.kgu.studywithme.studyreview.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.studyreview.application.usecase.command.DeleteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.command.UpdateStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.presentation.dto.request.UpdateStudyReviewRequest;
import com.kgu.studywithme.studyreview.presentation.dto.request.WriteStudyReviewRequest;
import com.kgu.studywithme.studyreview.presentation.dto.response.StudyReviewIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4-9. 스터디 리뷰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyReviewApiController {
    private final WriteStudyReviewUseCase writeStudyReviewUseCase;
    private final UpdateStudyReviewUseCase updateStudyReviewUseCase;
    private final DeleteStudyReviewUseCase deleteStudyReviewUseCase;

    @Operation(summary = "스터디 리뷰 작성 EndPoint")
    @PostMapping("/review")
    public ResponseEntity<StudyReviewIdResponse> write(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId,
            @RequestBody @Valid final WriteStudyReviewRequest request
    ) {
        final Long reviewId = writeStudyReviewUseCase.writeStudyReview(
                new WriteStudyReviewUseCase.Command(
                        studyId,
                        memberId,
                        request.content()
                )
        );
        return ResponseEntity.ok(new StudyReviewIdResponse(reviewId));
    }

    @Operation(summary = "스터디 리뷰 수정 EndPoint")
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId,
            @PathVariable final Long reviewId,
            @RequestBody @Valid final UpdateStudyReviewRequest request
    ) {
        updateStudyReviewUseCase.updateStudyReview(
                new UpdateStudyReviewUseCase.Command(
                        reviewId,
                        memberId,
                        request.content()
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 리뷰 삭제 EndPoint")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> delete(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId,
            @PathVariable final Long reviewId
    ) {
        deleteStudyReviewUseCase.deleteStudyReview(
                new DeleteStudyReviewUseCase.Command(
                        reviewId,
                        memberId
                )
        );
        return ResponseEntity.noContent().build();
    }
}
