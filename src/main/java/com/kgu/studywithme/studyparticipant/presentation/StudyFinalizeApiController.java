package com.kgu.studywithme.studyparticipant.presentation;

import com.kgu.studywithme.global.Authenticated;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.global.resolver.Auth;
import com.kgu.studywithme.studyparticipant.application.usecase.GraduateStudyUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.LeaveStudyUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.GraduateStudyCommand;
import com.kgu.studywithme.studyparticipant.application.usecase.command.LeaveStudyCommand;
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
    private final LeaveStudyUseCase leaveStudyUseCase;
    private final GraduateStudyUseCase graduateStudyUseCase;

    @Operation(summary = "스터디 떠나기 EndPoint")
    @CheckStudyParticipant
    @PatchMapping("/participants/leave")
    public ResponseEntity<Void> leave(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId
    ) {
        leaveStudyUseCase.invoke(new LeaveStudyCommand(studyId, authenticated.id()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 졸업 EndPoint")
    @CheckStudyParticipant
    @PatchMapping("/graduate")
    public ResponseEntity<Void> graduate(
            @Auth final Authenticated authenticated,
            @PathVariable final Long studyId
    ) {
        graduateStudyUseCase.invoke(new GraduateStudyCommand(studyId, authenticated.id()));
        return ResponseEntity.noContent().build();
    }
}
