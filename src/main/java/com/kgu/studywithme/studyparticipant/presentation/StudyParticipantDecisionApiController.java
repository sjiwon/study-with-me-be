package com.kgu.studywithme.studyparticipant.presentation;

import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.global.resolver.ExtractPayload;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApproveParticipationUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.RejectParticipationUseCase;
import com.kgu.studywithme.studyparticipant.presentation.dto.request.RejectParticipationRequest;
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

@Tag(name = "4-4-2. 스터디 신청자 참여 결정 API (팀장 전용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/applicants/{applierId}")
public class StudyParticipantDecisionApiController {
    private final ApproveParticipationUseCase approveParticipationUseCase;
    private final RejectParticipationUseCase rejectParticipationUseCase;

    @Operation(summary = "스터디 신청자 승인 EndPoint")
    @CheckStudyHost
    @PatchMapping("/approve")
    public ResponseEntity<Void> approve(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Long applierId
    ) {
        approveParticipationUseCase.invoke(
                new ApproveParticipationUseCase.Command(
                        studyId,
                        applierId
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 신청자 거절 EndPoint")
    @CheckStudyHost
    @PatchMapping("/reject")
    public ResponseEntity<Void> reject(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Long applierId,
            @RequestBody @Valid final RejectParticipationRequest request
    ) {
        rejectParticipationUseCase.invoke(
                new RejectParticipationUseCase.Command(
                        studyId,
                        applierId,
                        request.reason()
                )
        );
        return ResponseEntity.noContent().build();
    }
}
