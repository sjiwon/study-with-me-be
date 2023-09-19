package com.kgu.studywithme.studyweekly.presentation;

import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.global.resolver.ExtractPayload;
import com.kgu.studywithme.studyweekly.application.usecase.CreateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.DeleteStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.UpdateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.application.usecase.command.DeleteStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.Period;
import com.kgu.studywithme.studyweekly.presentation.dto.request.CreateStudyWeeklyRequest;
import com.kgu.studywithme.studyweekly.presentation.dto.request.UpdateStudyWeeklyRequest;
import com.kgu.studywithme.studyweekly.presentation.dto.response.StudyWeeklyIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "4-8-1. 스터디 주차 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/weeks")
public class StudyWeeklyApiController {
    private final CreateStudyWeeklyUseCase createStudyWeeklyUseCase;
    private final UpdateStudyWeeklyUseCase updateStudyWeeklyUseCase;
    private final DeleteStudyWeeklyUseCase deleteStudyWeeklyUseCase;

    @Operation(summary = "스터디 주차 생성 EndPoint")
    @CheckStudyHost
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudyWeeklyIdResponse> createWeekly(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @ModelAttribute @Valid final CreateStudyWeeklyRequest request
    ) {
        final Long weeklyId = createStudyWeeklyUseCase.invoke(new CreateStudyWeeklyCommand(
                studyId,
                hostId,
                request.title(),
                request.content(),
                new Period(request.startDate(), request.endDate()),
                request.assignmentExists(),
                request.autoAttendance(),
                FileConverter.convertAttachmentFiles(request.files())
        ));
        return ResponseEntity.ok(new StudyWeeklyIdResponse(weeklyId));
    }

    @Operation(summary = "스터디 주차 수정 EndPoint")
    @CheckStudyHost
    @PostMapping(value = "/{weeklyId}", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateWeekly(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Long weeklyId,
            @ModelAttribute @Valid final UpdateStudyWeeklyRequest request
    ) {
        updateStudyWeeklyUseCase.invoke(new UpdateStudyWeeklyCommand(
                weeklyId,
                request.title(),
                request.content(),
                new Period(request.startDate(), request.endDate()),
                request.assignmentExists(),
                request.autoAttendance(),
                FileConverter.convertAttachmentFiles(request.files())
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 주차 삭제 EndPoint")
    @CheckStudyHost
    @DeleteMapping("/{weeklyId}")
    public ResponseEntity<Void> deleteWeekly(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Long weeklyId
    ) {
        deleteStudyWeeklyUseCase.invoke(new DeleteStudyWeeklyCommand(studyId, weeklyId));
        return ResponseEntity.noContent().build();
    }
}
