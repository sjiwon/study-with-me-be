package com.kgu.studywithme.favorite.presentation;

import com.kgu.studywithme.favorite.application.usecase.CancelStudyLikeUseCase;
import com.kgu.studywithme.favorite.application.usecase.MarkStudyLikeUseCase;
import com.kgu.studywithme.favorite.application.usecase.command.CancelStudyLikeCommand;
import com.kgu.studywithme.favorite.application.usecase.command.MarkStudyLikeCommand;
import com.kgu.studywithme.global.aop.CheckAuthUser;
import com.kgu.studywithme.global.resolver.ExtractPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "6. 사용자 -> 스터디 찜 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/like")
public class FavoriteApiController {
    private final MarkStudyLikeUseCase markStudyLikeUseCase;
    private final CancelStudyLikeUseCase cancelStudyLikeUseCase;

    @Operation(summary = "스터디 찜 등록 EndPoint")
    @CheckAuthUser
    @PostMapping
    public ResponseEntity<Void> likeMarking(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId
    ) {
        markStudyLikeUseCase.invoke(new MarkStudyLikeCommand(memberId, studyId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 찜 등록 취소 EndPoint")
    @CheckAuthUser
    @DeleteMapping
    public ResponseEntity<Void> likeCancellation(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId
    ) {
        cancelStudyLikeUseCase.invoke(new CancelStudyLikeCommand(memberId, studyId));
        return ResponseEntity.noContent().build();
    }
}
