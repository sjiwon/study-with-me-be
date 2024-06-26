package com.kgu.studywithme.studyattendance.presentation;

import com.kgu.studywithme.auth.domain.model.Authenticated;
import com.kgu.studywithme.global.annotation.Auth;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.studyattendance.application.usecase.ManualAttendanceUseCase;
import com.kgu.studywithme.studyattendance.application.usecase.command.ManualAttendanceCommand;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import com.kgu.studywithme.studyattendance.presentation.dto.request.ManualAttendanceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4-5. 스터디 출석 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/attendance/{memberId}")
public class StudyAttendanceApiController {
    private final ManualAttendanceUseCase manualCheckAttendance;

    @Operation(summary = "스터디 출석 정보 수정 EndPoint")
    @CheckStudyHost
    @PatchMapping
    public ResponseEntity<Void> manualAttendance(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId,
            @PathVariable final Long memberId,
            @RequestBody @Valid final ManualAttendanceRequest request
    ) {
        manualCheckAttendance.invoke(new ManualAttendanceCommand(
                studyId,
                memberId,
                request.week(),
                AttendanceStatus.fromDescription(request.status())
        ));
        return ResponseEntity.noContent().build();
    }
}
