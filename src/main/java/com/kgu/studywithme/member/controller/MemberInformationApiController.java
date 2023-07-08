package com.kgu.studywithme.member.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckMemberIdentity;
import com.kgu.studywithme.member.service.MemberInformationService;
import com.kgu.studywithme.member.service.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "3-2. 사용자 정보 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
public class MemberInformationApiController {
    private final MemberInformationService memberInformationService;

    @Operation(summary = "사용자 기본 정보 조회 EndPoint")
    @GetMapping
    public ResponseEntity<MemberInformation> getInformation(@PathVariable Long memberId) {
        MemberInformation response = memberInformationService.getInformation(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자가 신청한 스터디 조회 EndPoint")
    @CheckMemberIdentity
    @GetMapping("/studies/apply")
    public ResponseEntity<RelatedStudy> getApplyStudy(@ExtractPayload Long payloadId, @PathVariable Long memberId) {
        RelatedStudy response = memberInformationService.getApplyStudy(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자가 찜한 스터디 조회 EndPoint")
    @CheckMemberIdentity
    @GetMapping("/studies/favorite")
    public ResponseEntity<RelatedStudy> getFavoriteStudy(@ExtractPayload Long payloadId, @PathVariable Long memberId) {
        RelatedStudy response = memberInformationService.getFavoriteStudy(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자가 참여하고 있는 스터디 조회 EndPoint")
    @GetMapping("/studies/participate")
    public ResponseEntity<RelatedStudy> getParticipateStudy(@PathVariable Long memberId) {
        RelatedStudy response = memberInformationService.getParticipateStudy(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자가 졸업한 스터디 조회 EndPoint")
    @GetMapping("/studies/graduated")
    public ResponseEntity<GraduatedStudy> getGraduatedStudy(@PathVariable Long memberId) {
        GraduatedStudy response = memberInformationService.getGraduatedStudy(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자가 받은 피어리뷰 조회 EndPoint")
    @GetMapping("/reviews")
    public ResponseEntity<PeerReviewAssembler> getReviews(@PathVariable Long memberId) {
        PeerReviewAssembler response = memberInformationService.getPeerReviews(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 출석률 조회 EndPoint")
    @GetMapping("/attendances")
    public ResponseEntity<AttendanceRatioAssembler> getAttendanceRatio(@PathVariable Long memberId) {
        AttendanceRatioAssembler response = memberInformationService.getAttendanceRatio(memberId);
        return ResponseEntity.ok(response);
    }
}
