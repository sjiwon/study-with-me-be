package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckMemberIdentity;
import com.kgu.studywithme.member.application.MemberService;
import com.kgu.studywithme.member.controller.dto.request.MemberReportRequest;
import com.kgu.studywithme.member.controller.dto.request.MemberUpdateRequest;
import com.kgu.studywithme.member.controller.dto.request.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "3-1. 사용자 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberApiController {
    private final MemberService memberService;

    @Operation(summary = "회원가입 EndPoint")
    @PostMapping("/member")
    public ResponseEntity<Void> signUp(@RequestBody @Valid final SignUpRequest request) {
        final Long savedMemberId = memberService.signUp(request.toEntity());

        return ResponseEntity
                .created(
                        UriComponentsBuilder
                                .fromPath("/api/members/{id}")
                                .build(savedMemberId)
                )
                .build();
    }

    @Operation(summary = "사용자 정보 수정 EndPoint")
    @CheckMemberIdentity
    @PatchMapping("/members/{memberId}")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long payloadId,
            @PathVariable final Long memberId,
            @RequestBody @Valid final MemberUpdateRequest request
    ) {
        memberService.update(memberId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 신고 EndPoint")
    @PostMapping("/members/{reporteeId}/report")
    public ResponseEntity<Void> report(
            @ExtractPayload final Long reporterId,
            @PathVariable final Long reporteeId,
            @RequestBody @Valid final MemberReportRequest request
    ) {
        memberService.report(reporteeId, reporterId, request.reason());
        return ResponseEntity.noContent().build();
    }
}
