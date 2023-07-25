package com.kgu.studywithme.studyweekly.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.DeleteStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.Period;
import com.kgu.studywithme.studyweekly.presentation.dto.request.CreateStudyWeeklyRequest;
import com.kgu.studywithme.studyweekly.presentation.dto.request.UpdateStudyWeeklyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "4-8-1. 스터디 주차 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyWeeklyApiController {
    private final CreateStudyWeeklyUseCase createStudyWeeklyUseCase;
    private final UpdateStudyWeeklyUseCase updateStudyWeeklyUseCase;
    private final DeleteStudyWeeklyUseCase deleteStudyWeeklyUseCase;

    @Operation(summary = "스터디 주차 생성 EndPoint")
    @CheckStudyHost
    @PostMapping(value = "/week", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createWeekly(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @ModelAttribute @Valid final CreateStudyWeeklyRequest request
    ) {
        createStudyWeeklyUseCase.createStudyWeekly(
                new CreateStudyWeeklyUseCase.Command(
                        studyId,
                        hostId,
                        request.title(),
                        request.content(),
                        new Period(request.startDate(), request.endDate()),
                        request.assignmentExists(),
                        request.autoAttendance(),
                        request.files()
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 주차 수정 EndPoint")
    @CheckStudyHost
    @PostMapping(value = "/weeks/{week}", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateWeekly(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Integer week,
            @ModelAttribute @Valid final UpdateStudyWeeklyRequest request
    ) {
        updateStudyWeeklyUseCase.updateStudyWeekly(
                new UpdateStudyWeeklyUseCase.Command(
                        studyId,
                        week,
                        request.title(),
                        request.content(),
                        new Period(request.startDate(), request.endDate()),
                        request.assignmentExists(),
                        request.autoAttendance(),
                        request.files()
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 주차 삭제 EndPoint")
    @CheckStudyHost
    @DeleteMapping("/weeks/{week}")
    public ResponseEntity<Void> deleteWeekly(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Integer week
    ) {
        deleteStudyWeeklyUseCase.deleteStudyWeekly(
                new DeleteStudyWeeklyUseCase.Command(
                        studyId,
                        week
                )
        );
        return ResponseEntity.noContent().build();
    }
}
