package com.kgu.studywithme.studyweekly.presentation;

import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.global.Authenticated;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.global.resolver.Auth;
import com.kgu.studywithme.studyweekly.application.usecase.EditWeeklySubmittedAssignmentUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditWeeklySubmittedAssignmentCommand;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentCommand;
import com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType;
import com.kgu.studywithme.studyweekly.presentation.dto.request.EditSubmittedWeeklyAssignmentRequest;
import com.kgu.studywithme.studyweekly.presentation.dto.request.SubmitWeeklyAssignmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "4-8-2. 스터디 주차별 과제 제출 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/weeks/{weeklyId}/assignment")
public class StudyWeeklySubmitApiController {
    private final SubmitWeeklyAssignmentUseCase submitWeeklyAssignmentUseCase;
    private final EditWeeklySubmittedAssignmentUseCase editWeeklySubmittedAssignmentUseCase;

    @Operation(summary = "스터디 주차별 과제 제출 EndPoint")
    @CheckStudyParticipant
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> submitWeeklyAssignment(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId,
            @PathVariable final Long weeklyId,
            @ModelAttribute @Valid final SubmitWeeklyAssignmentRequest request
    ) {
        submitWeeklyAssignmentUseCase.invoke(new SubmitWeeklyAssignmentCommand(
                authenticated.id(),
                studyId,
                weeklyId,
                AssignmentSubmitType.from(request.type()),
                FileConverter.convertAssignmentFile(request.file()),
                request.link()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 주차별 제출한 과제 수정 EndPoint")
    @CheckStudyParticipant
    @PostMapping(value = "/edit", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> editSubmittedWeeklyAssignment(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId,
            @PathVariable final Long weeklyId,
            @ModelAttribute @Valid final EditSubmittedWeeklyAssignmentRequest request
    ) {
        editWeeklySubmittedAssignmentUseCase.invoke(new EditWeeklySubmittedAssignmentCommand(
                authenticated.id(),
                studyId,
                weeklyId,
                AssignmentSubmitType.from(request.type()),
                FileConverter.convertAssignmentFile(request.file()),
                request.link()
        ));
        return ResponseEntity.noContent().build();
    }
}
