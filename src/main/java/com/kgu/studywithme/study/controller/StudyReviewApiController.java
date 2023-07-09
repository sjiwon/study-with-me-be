package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.study.application.StudyReviewService;
import com.kgu.studywithme.study.controller.dto.request.ReviewRequest;
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
    private final StudyReviewService studyReviewService;

    @Operation(summary = "스터디 리뷰 등록 EndPoint")
    @PostMapping("/review")
    public ResponseEntity<Void> write(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId,
            @RequestBody @Valid final ReviewRequest request
    ) {
        studyReviewService.write(studyId, memberId, request.content());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 리뷰 삭제 EndPoint")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> remove(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId,
            @PathVariable final Long reviewId
    ) {
        studyReviewService.remove(reviewId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 리뷰 수정 EndPoint")
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId,
            @PathVariable final Long reviewId,
            @RequestBody @Valid final ReviewRequest request
    ) {
        studyReviewService.update(reviewId, memberId, request.content());
        return ResponseEntity.noContent().build();
    }
}
