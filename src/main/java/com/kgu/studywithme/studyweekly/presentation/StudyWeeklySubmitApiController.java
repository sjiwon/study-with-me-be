package com.kgu.studywithme.studyweekly.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditSubmittedWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
        submitWeeklyAssignmentUseCase.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        memberId,
                        studyId,
                        weeklyId,
                        AssignmentSubmitType.from(request.type()),
                        extractFileData(request.file()),
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
        editSubmittedWeeklyAssignmentUseCase.invoke(
                new EditSubmittedWeeklyAssignmentUseCase.Command(
                        memberId,
                        studyId,
                        weeklyId,
                        AssignmentSubmitType.from(request.type()),
                        extractFileData(request.file()),
                        request.link()
                )
        );
        return ResponseEntity.noContent().build();
    }

    private RawFileData extractFileData(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            return new RawFileData(
                    file.getInputStream(),
                    file.getContentType(),
                    file.getOriginalFilename()
            );
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
