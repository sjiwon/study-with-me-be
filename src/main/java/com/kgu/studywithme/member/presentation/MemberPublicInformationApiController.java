package com.kgu.studywithme.member.presentation;

import com.kgu.studywithme.global.dto.ResponseWrapper;
import com.kgu.studywithme.member.application.usecase.MemberPublicQueryUseCase;
import com.kgu.studywithme.member.application.usecase.query.GetAttendanceRatioById;
import com.kgu.studywithme.member.application.usecase.query.GetGraduatedStudyById;
import com.kgu.studywithme.member.application.usecase.query.GetParticipateStudyById;
import com.kgu.studywithme.member.application.usecase.query.GetPublicInformationById;
import com.kgu.studywithme.member.application.usecase.query.GetReceivedReviewById;
import com.kgu.studywithme.member.domain.repository.query.dto.AttendanceRatio;
import com.kgu.studywithme.member.domain.repository.query.dto.GraduatedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPublicInformation;
import com.kgu.studywithme.member.domain.repository.query.dto.ParticipateStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.ReceivedReview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "3-2-1. 사용자 Public 정보 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
public class MemberPublicInformationApiController {
    private final MemberPublicQueryUseCase memberPublicQueryUseCase;

    @Operation(summary = "사용자 기본 Public 정보 조회 EndPoint")
    @GetMapping
    public ResponseEntity<MemberPublicInformation> getInformation(@PathVariable final Long memberId) {
        final MemberPublicInformation response = memberPublicQueryUseCase.getPublicInformationById(new GetPublicInformationById(memberId));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자가 참여하고 있는 스터디 조회 EndPoint")
    @GetMapping("/studies/participate")
    public ResponseEntity<ResponseWrapper<List<ParticipateStudy>>> getParticipateStudy(@PathVariable final Long memberId) {
        final List<ParticipateStudy> response = memberPublicQueryUseCase.getParticipateStudyById(new GetParticipateStudyById(memberId));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "사용자가 졸업한 스터디 조회 EndPoint")
    @GetMapping("/studies/graduated")
    public ResponseEntity<ResponseWrapper<List<GraduatedStudy>>> getGraduatedStudy(@PathVariable final Long memberId) {
        final List<GraduatedStudy> response = memberPublicQueryUseCase.getGraduatedStudyById(new GetGraduatedStudyById(memberId));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "사용자가 받은 리뷰 조회 EndPoint")
    @GetMapping("/reviews")
    public ResponseEntity<ResponseWrapper<List<ReceivedReview>>> getReviews(@PathVariable final Long memberId) {
        final List<ReceivedReview> response = memberPublicQueryUseCase.getReceivedReviewById(new GetReceivedReviewById(memberId));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "사용자 출석률 조회 EndPoint")
    @GetMapping("/attendances")
    public ResponseEntity<ResponseWrapper<List<AttendanceRatio>>> getAttendanceRatio(@PathVariable final Long memberId) {
        final List<AttendanceRatio> response = memberPublicQueryUseCase.getAttendanceRatioById(new GetAttendanceRatioById(memberId));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }
}
