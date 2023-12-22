package com.kgu.studywithme.studyparticipant.presentation;

import com.kgu.studywithme.auth.domain.model.Authenticated;
import com.kgu.studywithme.global.annotation.Auth;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.studyparticipant.application.usecase.ApproveParticipationUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.RejectParticipationUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApproveParticipationCommand;
import com.kgu.studywithme.studyparticipant.application.usecase.command.RejectParticipationCommand;
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
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId,
            @PathVariable final Long applierId
    ) {
        approveParticipationUseCase.invoke(new ApproveParticipationCommand(studyId, applierId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 신청자 거절 EndPoint")
    @CheckStudyHost
    @PatchMapping("/reject")
    public ResponseEntity<Void> reject(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId,
            @PathVariable final Long applierId,
            @RequestBody @Valid final RejectParticipationRequest request
    ) {
        rejectParticipationUseCase.invoke(new RejectParticipationCommand(studyId, applierId, request.reason()));
        return ResponseEntity.noContent().build();
    }
}
