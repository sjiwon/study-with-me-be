package com.kgu.studywithme.favorite.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.favorite.application.FavoriteManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "6. 사용자 -> 스터디 찜 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/like")
public class FavoriteApiController {
    private final FavoriteManageService favoriteManageService;

    @Operation(summary = "스터디 찜 등록 EndPoint")
    @PostMapping
    public ResponseEntity<Void> like(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId
    ) {
        favoriteManageService.like(studyId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 찜 등록 취소 EndPoint")
    @DeleteMapping
    public ResponseEntity<Void> cancel(
            @ExtractPayload final Long memberId,
            @PathVariable final Long studyId
    ) {
        favoriteManageService.cancel(studyId, memberId);
        return ResponseEntity.noContent().build();
    }
}
