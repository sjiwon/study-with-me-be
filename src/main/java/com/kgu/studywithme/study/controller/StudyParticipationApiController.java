package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.study.controller.dto.request.ParticipationRejectRequest;
import com.kgu.studywithme.study.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4-4. 스터디 참여 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyParticipationApiController {
    private final ParticipationService participationService;

    @Operation(summary = "스터디 신청 EndPoint")
    @PostMapping("/applicants")
    public ResponseEntity<Void> apply(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        participationService.apply(studyId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 신청 취소 EndPoint")
    @DeleteMapping("/applicants")
    public ResponseEntity<Void> applyCancel(@ExtractPayload Long applierId, @PathVariable Long studyId) {
        participationService.applyCancel(studyId, applierId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 신청자 승인 EndPoint")
    @CheckStudyHost
    @PatchMapping("/applicants/{applierId}/approve")
    public ResponseEntity<Void> approve(@ExtractPayload Long hostId,
                                        @PathVariable Long studyId,
                                        @PathVariable Long applierId) {
        participationService.approve(studyId, applierId, hostId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 신청자 거부 EndPoint")
    @CheckStudyHost
    @PatchMapping("/applicants/{applierId}/reject")
    public ResponseEntity<Void> reject(@ExtractPayload Long hostId,
                                       @PathVariable Long studyId,
                                       @PathVariable Long applierId,
                                       @RequestBody @Valid ParticipationRejectRequest request) {
        participationService.reject(studyId, applierId, hostId, request.reason());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 참여 취소 EndPoint")
    @CheckStudyParticipant
    @PatchMapping("/participants/cancel")
    public ResponseEntity<Void> cancel(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        participationService.cancel(studyId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 팀장 권한 위임 EndPoint")
    @CheckStudyHost
    @PatchMapping("/participants/{participantId}/delegation")
    public ResponseEntity<Void> delegateAuthority(@ExtractPayload Long hostId,
                                                  @PathVariable Long studyId,
                                                  @PathVariable Long participantId) {
        participationService.delegateAuthority(studyId, participantId, hostId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 졸업 EndPoint")
    @CheckStudyParticipant
    @PatchMapping("/graduate")
    public ResponseEntity<Void> graduate(@ExtractPayload Long memberId, @PathVariable Long studyId) {
        participationService.graduate(studyId, memberId);
        return ResponseEntity.noContent().build();
    }
}
