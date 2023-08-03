package com.kgu.studywithme.studyweekly.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditSubmittedWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.presentation.dto.request.EditSubmittedWeeklyAssignmentRequest;
import com.kgu.studywithme.studyweekly.presentation.dto.request.SubmitWeeklyAssignmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "4-8-2. 스터디 주차별 과제 제출 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/weeks/{weeklyId}/assignment")
public class StudyWeeklySubmitApiController {
    private final SubmitWeeklyAssignmentUseCase submitWeeklyAssignmentUseCase;
    private final EditSubmittedWeeklyAssignmentUseCase editSubmittedWeeklyAssignmentUseCase;

    @Operation(summary = "스터디 주차별 과제 제출 EndPoint")
    @CheckStudyParticipant
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> submitWeeklyAssignment(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId,
            @PathVariable final Long weeklyId,
            @ModelAttribute @Valid final SubmitWeeklyAssignmentRequest request
    ) {
        submitWeeklyAssignmentUseCase.submitWeeklyAssignment(
                new SubmitWeeklyAssignmentUseCase.Command(
                        memberId,
                        studyId,
                        weeklyId,
                        request.type(),
                        request.file(),
                        request.link()
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 주차별 제출한 과제 수정 EndPoint")
    @CheckStudyParticipant
    @PostMapping(value = "/edit", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> editSubmittedWeeklyAssignment(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId,
            @PathVariable final Long weeklyId,
            @ModelAttribute @Valid final EditSubmittedWeeklyAssignmentRequest request
    ) {
        editSubmittedWeeklyAssignmentUseCase.editSubmittedWeeklyAssignment(
                new EditSubmittedWeeklyAssignmentUseCase.Command(
                        memberId,
                        studyId,
                        weeklyId,
                        request.type(),
                        request.file(),
                        request.link()
                )
        );
        return ResponseEntity.noContent().build();
    }
}
