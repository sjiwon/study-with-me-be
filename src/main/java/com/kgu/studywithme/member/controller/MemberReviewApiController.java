package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.member.controller.dto.request.MemberReviewRequest;
import com.kgu.studywithme.member.service.MemberReviewService;
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
    private final MemberReviewService memberReviewService;

    @Operation(summary = "피어리뷰 작성 EndPoint")
    @PostMapping
    public ResponseEntity<Void> writeReview(@ExtractPayload Long reviewerId,
                                            @PathVariable Long revieweeId,
                                            @RequestBody @Valid MemberReviewRequest request) {
        memberReviewService.writeReview(revieweeId, reviewerId, request.content());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "피어리뷰 수정 EndPoint")
    @PatchMapping
    public ResponseEntity<Void> updateReview(@ExtractPayload Long reviewerId,
                                             @PathVariable Long revieweeId,
                                             @RequestBody @Valid MemberReviewRequest request) {
        memberReviewService.updateReview(revieweeId, reviewerId, request.content());
        return ResponseEntity.noContent().build();
    }
}
