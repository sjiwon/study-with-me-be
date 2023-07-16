package com.kgu.studywithme.peerreview.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.peerreview.application.usecase.command.UpdatePeerReviewUseCase;
import com.kgu.studywithme.peerreview.application.usecase.command.WritePeerReviewUseCase;
import com.kgu.studywithme.peerreview.presentation.dto.request.UpdatePeerReviewRequest;
import com.kgu.studywithme.peerreview.presentation.dto.request.WritePeerReviewRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3-3. 사용자 피어리뷰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{revieweeId}/review")
public class MemberReviewApiController {
    private final WritePeerReviewUseCase writePeerReviewUseCase;
    private final UpdatePeerReviewUseCase updatePeerReviewUseCase;

    @Operation(summary = "피어리뷰 작성 EndPoint")
    @PostMapping
    public ResponseEntity<Void> writeReview(
            @ExtractPayload final Long reviewerId,
            @PathVariable final Long revieweeId,
            @RequestBody @Valid final WritePeerReviewRequest request
    ) {
        writePeerReviewUseCase.writePeerReview(
                new WritePeerReviewUseCase.Command(reviewerId, revieweeId, request.content())
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "피어리뷰 수정 EndPoint")
    @PatchMapping
    public ResponseEntity<Void> updateReview(
            @ExtractPayload final Long reviewerId,
            @PathVariable final Long revieweeId,
            @RequestBody @Valid final UpdatePeerReviewRequest request
    ) {
        updatePeerReviewUseCase.updatePeerReview(
                new UpdatePeerReviewUseCase.Command(reviewerId, revieweeId, request.content())
        );
        return ResponseEntity.noContent().build();
    }
}
