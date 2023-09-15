package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.global.dto.ResponseWrapper;
import com.kgu.studywithme.global.resolver.ExtractPayload;
import com.kgu.studywithme.study.application.usecase.query.QueryApplicantByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryAttendanceByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryNoticeByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryWeeklyByIdUseCase;
import com.kgu.studywithme.study.infrastructure.query.dto.AttendanceInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.NoticeInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyApplicantInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.WeeklyInformation;
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
    private final QueryApplicantByIdUseCase queryApplicantByIdUseCase;
    private final QueryNoticeByIdUseCase queryNoticeByIdUseCase;
    private final QueryAttendanceByIdUseCase queryAttendanceByIdUseCase;
    private final QueryWeeklyByIdUseCase queryWeeklyByIdUseCase;

    @Operation(summary = "스터디 신청자 조회 EndPoint")
    @CheckStudyHost
    @GetMapping("/applicants")
    public ResponseEntity<ResponseWrapper<List<StudyApplicantInformation>>> getApplicants(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId
    ) {
        final List<StudyApplicantInformation> response = queryApplicantByIdUseCase.invoke(
                new QueryApplicantByIdUseCase.Query(studyId)
        );
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "스터디 공지사항 조회 EndPoint")
    @CheckStudyParticipant
    @GetMapping("/notices")
    public ResponseEntity<ResponseWrapper<List<NoticeInformation>>> getNotices(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId
    ) {
        final List<NoticeInformation> response = queryNoticeByIdUseCase.invoke(
                new QueryNoticeByIdUseCase.Query(studyId)
        );
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "스터디 출석 정보 조회 EndPoint")
    @CheckStudyParticipant
    @GetMapping("/attendances")
    public ResponseEntity<ResponseWrapper<List<AttendanceInformation>>> getAttendances(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId
    ) {
        final List<AttendanceInformation> response = queryAttendanceByIdUseCase.invoke(
                new QueryAttendanceByIdUseCase.Query(studyId)
        );
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "스터디 주차별 정보 조회 EndPoint")
    @CheckStudyParticipant
    @GetMapping("/weeks")
    public ResponseEntity<ResponseWrapper<List<WeeklyInformation>>> getWeeks(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId
    ) {
        final List<WeeklyInformation> response = queryWeeklyByIdUseCase.invoke(
                new QueryWeeklyByIdUseCase.Query(studyId)
        );
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }
}
