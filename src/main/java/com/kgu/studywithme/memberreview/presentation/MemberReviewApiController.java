package com.kgu.studywithme.memberreview.presentation;

import com.kgu.studywithme.global.Authenticated;
import com.kgu.studywithme.global.aop.CheckAuthUser;
import com.kgu.studywithme.global.resolver.Auth;
import com.kgu.studywithme.memberreview.application.usecase.UpdateMemberReviewUseCase;
import com.kgu.studywithme.memberreview.application.usecase.WriteMemberReviewUseCase;
import com.kgu.studywithme.memberreview.application.usecase.command.UpdateMemberReviewCommand;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewCommand;
import com.kgu.studywithme.memberreview.presentation.dto.request.UpdateMemberReviewRequest;
import com.kgu.studywithme.memberreview.presentation.dto.request.WriteMemberReviewRequest;
import com.kgu.studywithme.memberreview.presentation.dto.response.MemberReviewIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @Auth final Authenticated authenticated,
            @PathVariable final Long revieweeId,
            @RequestBody @Valid final WriteMemberReviewRequest request
    ) {
        final Long reviewId = writeMemberReviewUseCase.invoke(new WriteMemberReviewCommand(authenticated.id(), revieweeId, request.content()));
        return ResponseEntity.ok(new MemberReviewIdResponse(reviewId));
    }

    @Operation(summary = "피어리뷰 수정 EndPoint")
    @CheckAuthUser
    @PatchMapping
    public ResponseEntity<Void> updateMemberReview(
            @Auth final Authenticated authenticated,
            @PathVariable final Long revieweeId,
            @RequestBody @Valid final UpdateMemberReviewRequest request
    ) {
        updateMemberReviewUseCase.invoke(new UpdateMemberReviewCommand(authenticated.id(), revieweeId, request.content()));
        return ResponseEntity.noContent().build();
    }
}
