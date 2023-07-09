package com.kgu.studywithme.study.controller.attendance;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.study.application.attendance.AttendanceService;
import com.kgu.studywithme.study.controller.dto.request.AttendanceRequest;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4-5. 스터디 출석 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/attendance/{memberId}")
public class AttendanceApiController {
    private final AttendanceService attendanceService;

    @Operation(summary = "스터디 출석 정보 수정 EndPoint")
    @CheckStudyHost
    @PatchMapping
    public ResponseEntity<Void> manualCheckAttendance(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Long memberId,
            @RequestBody @Valid final AttendanceRequest request
    ) {
        attendanceService.manualCheckAttendance(studyId, memberId, request.week(), AttendanceStatus.fromDescription(request.status()));
        return ResponseEntity.noContent().build();
    }
}
