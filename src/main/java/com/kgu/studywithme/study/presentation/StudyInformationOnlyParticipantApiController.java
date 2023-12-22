package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.auth.domain.model.Authenticated;
import com.kgu.studywithme.global.annotation.Auth;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.global.dto.ResponseWrapper;
import com.kgu.studywithme.study.application.usecase.StudyQueryOnlyParticipantUseCase;
import com.kgu.studywithme.study.application.usecase.query.GetApplicantById;
import com.kgu.studywithme.study.application.usecase.query.GetAttendanceById;
import com.kgu.studywithme.study.application.usecase.query.GetNoticeById;
import com.kgu.studywithme.study.application.usecase.query.GetWeeklyById;
import com.kgu.studywithme.study.domain.repository.query.dto.AttendanceInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.NoticeInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyApplicantInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.WeeklyInformation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "4-3-2. 스터디 정보 조회 API [참여자 전용]")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyInformationOnlyParticipantApiController {
    private final StudyQueryOnlyParticipantUseCase studyQueryOnlyParticipantUseCase;

    @Operation(summary = "스터디 신청자 조회 EndPoint")
    @CheckStudyHost
    @GetMapping("/applicants")
    public ResponseEntity<ResponseWrapper<List<StudyApplicantInformation>>> getApplicants(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId
    ) {
        final List<StudyApplicantInformation> response = studyQueryOnlyParticipantUseCase.getApplicantById(new GetApplicantById(studyId));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "스터디 공지사항 조회 EndPoint")
    @CheckStudyParticipant
    @GetMapping("/notices")
    public ResponseEntity<ResponseWrapper<List<NoticeInformation>>> getNotices(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId
    ) {
        final List<NoticeInformation> response = studyQueryOnlyParticipantUseCase.getNoticeById(new GetNoticeById(studyId));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "스터디 출석 정보 조회 EndPoint")
    @CheckStudyParticipant
    @GetMapping("/attendances")
    public ResponseEntity<ResponseWrapper<List<AttendanceInformation>>> getAttendances(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId
    ) {
        final List<AttendanceInformation> response = studyQueryOnlyParticipantUseCase.getAttendanceById(new GetAttendanceById(studyId));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "스터디 주차별 정보 조회 EndPoint")
    @CheckStudyParticipant
    @GetMapping("/weeks")
    public ResponseEntity<ResponseWrapper<List<WeeklyInformation>>> getWeeks(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId
    ) {
        final List<WeeklyInformation> response = studyQueryOnlyParticipantUseCase.getWeeklyById(new GetWeeklyById(studyId));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }
}
