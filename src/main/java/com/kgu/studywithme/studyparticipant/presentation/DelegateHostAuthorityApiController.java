package com.kgu.studywithme.studyparticipant.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.studyparticipant.application.usecase.command.DelegateHostAuthorityUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4-4-3. 스터디 팀장 권한 위임 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/participants/{participantId}/delegation")
public class DelegateHostAuthorityApiController {
    private final DelegateHostAuthorityUseCase delegateHostAuthorityUseCase;

    @Operation(summary = "스터디 팀장 권한 위임 EndPoint")
    @CheckStudyHost
    @PatchMapping
    public ResponseEntity<Void> delegateAuthority(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Long participantId
    ) {
        delegateHostAuthorityUseCase.invoke(
                new DelegateHostAuthorityUseCase.Command(
                        studyId,
                        participantId
                )
        );
        return ResponseEntity.noContent().build();
    }
}
