package com.kgu.studywithme.memberreview.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckAuthUser;
import com.kgu.studywithme.memberreview.application.usecase.command.UpdateMemberReviewUseCase;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewUseCase;
import com.kgu.studywithme.memberreview.presentation.dto.request.UpdateMemberReviewRequest;
import com.kgu.studywithme.memberreview.presentation.dto.request.WriteMemberReviewRequest;
import com.kgu.studywithme.memberreview.presentation.dto.response.MemberReviewIdResponse;
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
    private final WriteMemberReviewUseCase writeMemberReviewUseCase;
    private final UpdateMemberReviewUseCase updateMemberReviewUseCase;

    @Operation(summary = "피어리뷰 작성 EndPoint")
    @CheckAuthUser
    @PostMapping
    public ResponseEntity<MemberReviewIdResponse> writeMemberReview(
            @ExtractPayload final Long reviewerId,
            @PathVariable final Long revieweeId,
            @RequestBody @Valid final WriteMemberReviewRequest request
    ) {
        final Long reviewId = writeMemberReviewUseCase.writeMemberReview(
                new WriteMemberReviewUseCase.Command(reviewerId, revieweeId, request.content())
        );
        return ResponseEntity.ok(new MemberReviewIdResponse(reviewId));
    }

    @Operation(summary = "피어리뷰 수정 EndPoint")
    @CheckAuthUser
    @PatchMapping
    public ResponseEntity<Void> updateMemberReview(
            @ExtractPayload final Long reviewerId,
            @PathVariable final Long revieweeId,
            @RequestBody @Valid final UpdateMemberReviewRequest request
    ) {
        updateMemberReviewUseCase.updateMemberReview(
                new UpdateMemberReviewUseCase.Command(reviewerId, revieweeId, request.content())
        );
        return ResponseEntity.noContent().build();
    }
}
