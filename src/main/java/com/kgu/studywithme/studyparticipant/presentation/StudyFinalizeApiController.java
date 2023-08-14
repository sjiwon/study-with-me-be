package com.kgu.studywithme.studyparticipant.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.studyparticipant.application.usecase.command.GraduateStudyUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.LeaveParticipationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4-4-4. 스터디 참여 마무리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyFinalizeApiController {
    private final LeaveParticipationUseCase leaveParticipationUseCase;
    private final GraduateStudyUseCase graduateStudyUseCase;

    @Operation(summary = "스터디 참여 취소 EndPoint")
    @CheckStudyParticipant
    @PatchMapping("/participants/leave")
    public ResponseEntity<Void> leave(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId
    ) {
        leaveParticipationUseCase.invoke(
                new LeaveParticipationUseCase.Command(
                        studyId,
                        memberId
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 졸업 EndPoint")
    @CheckStudyParticipant
    @PatchMapping("/graduate")
    public ResponseEntity<Void> graduate(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId
    ) {
        graduateStudyUseCase.invoke(
                new GraduateStudyUseCase.Command(
                        studyId,
                        memberId
                )
        );
        return ResponseEntity.noContent().build();
    }
}
