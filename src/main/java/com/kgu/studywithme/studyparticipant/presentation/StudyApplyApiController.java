package com.kgu.studywithme.studyparticipant.presentation;

import com.kgu.studywithme.global.Authenticated;
import com.kgu.studywithme.global.resolver.Auth;
import com.kgu.studywithme.studyparticipant.application.usecase.ApplyCancelUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.ApplyStudyUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyCancelCommand;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyStudyCommand;
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
    private final ApplyCancelUseCase applyCancelUseCase;

    @Operation(summary = "스터디 참여 신청 EndPoint")
    @PostMapping
    public ResponseEntity<Void> apply(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId
    ) {
        applyStudyUseCase.invoke(new ApplyStudyCommand(studyId, authenticated.id()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 참여 신청 취소 EndPoint")
    @DeleteMapping
    public ResponseEntity<Void> applyCancellation(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId
    ) {
        applyCancelUseCase.invoke(new ApplyCancelCommand(studyId, authenticated.id()));
        return ResponseEntity.noContent().build();
    }
}
