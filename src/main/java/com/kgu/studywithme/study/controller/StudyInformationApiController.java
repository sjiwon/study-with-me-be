package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.study.service.StudyInformationService;
import com.kgu.studywithme.study.service.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4-3. 스터디 정보 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyInformationApiController {
    private final StudyInformationService studyInformationService;

    @Operation(summary = "스터디 기본 정보 조회 EndPoint")
    @GetMapping
    public ResponseEntity<StudyInformation> getInformation(@PathVariable Long studyId) {
        StudyInformation response = studyInformationService.getInformation(studyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스터디 리뷰 조회 EndPoint")
    @GetMapping("/reviews")
    public ResponseEntity<ReviewAssembler> getReviews(@PathVariable Long studyId) {
        ReviewAssembler response = studyInformationService.getReviews(studyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스터디 공지사항 조회 EndPoint")
    @CheckStudyParticipant
    @GetMapping("/notices")
    public ResponseEntity<NoticeAssembler> getNotices(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        NoticeAssembler response = studyInformationService.getNotices(studyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스터디 신청자 조회 EndPoint")
    @CheckStudyHost
    @GetMapping("/applicants")
    public ResponseEntity<StudyApplicant> getApplicants(@ExtractPayload Long hostId, @PathVariable Long studyId) {
        StudyApplicant response = studyInformationService.getApplicants(studyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스터디 참여자 조회 EndPoint")
    @GetMapping("/participants")
    public ResponseEntity<StudyParticipant> getApproveParticipants(@ExtractPayload Long hostId, @PathVariable Long studyId) {
        StudyParticipant response = studyInformationService.getApproveParticipants(studyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스터디 출석 정보 조회 EndPoint")
    @CheckStudyParticipant
    @GetMapping("/attendances")
    public ResponseEntity<AttendanceAssmbler> getAttendances(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        AttendanceAssmbler response = studyInformationService.getAttendances(studyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스터디 주차별 정보 조회 EndPoint")
    @CheckStudyParticipant
    @GetMapping("/weeks")
    public ResponseEntity<WeeklyAssembler> getWeeks(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        WeeklyAssembler response = studyInformationService.getWeeks(studyId);
        return ResponseEntity.ok(response);
    }
}
