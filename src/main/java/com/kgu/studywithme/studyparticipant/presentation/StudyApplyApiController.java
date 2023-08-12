package com.kgu.studywithme.studyparticipant.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyCancellationUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyStudyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4-4-1. 스터디 참여 신청 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/applicants")
public class StudyApplyApiController {
    private final ApplyStudyUseCase applyStudyUseCase;
    private final ApplyCancellationUseCase applyCancellationUseCase;

    @Operation(summary = "스터디 참여 신청 EndPoint")
    @PostMapping
    public ResponseEntity<Void> apply(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId
    ) {
        applyStudyUseCase.apply(
                new ApplyStudyUseCase.Command(
                        studyId,
                        memberId
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 참여 신청 취소 EndPoint")
    @DeleteMapping
    public ResponseEntity<Void> applyCancellation(
            @ExtractPayload final Long applierId,
            @PathVariable final Long studyId
    ) {
        applyCancellationUseCase.applyCancellation(
                new ApplyCancellationUseCase.Command(
                        studyId,
                        applierId
                )
        );
        return ResponseEntity.noContent().build();
    }
}
