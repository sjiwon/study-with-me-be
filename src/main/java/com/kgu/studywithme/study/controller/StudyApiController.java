package com.kgu.studywithme.study.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.study.controller.dto.request.StudyRegisterRequest;
import com.kgu.studywithme.study.controller.dto.request.StudyUpdateRequest;
import com.kgu.studywithme.study.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "4-1. 스터디 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyApiController {
    private final StudyService studyService;

    @Operation(summary = "스터디 생성 EndPoint")
    @PostMapping("/study")
    public ResponseEntity<Void> register(
            @ExtractPayload final Long hostId,
            @RequestBody @Valid final StudyRegisterRequest request
    ) {
        final Long savedStudyId = studyService.register(hostId, request);

        return ResponseEntity
                .created(
                        UriComponentsBuilder
                                .fromPath("/api/studies/{id}")
                                .build(savedStudyId)
                )
                .build();
    }

    @Operation(summary = "스터디 수정 EndPoint")
    @CheckStudyHost
    @PatchMapping("/studies/{studyId}")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @RequestBody @Valid final StudyUpdateRequest request
    ) {
        studyService.update(studyId, hostId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 종료 EndPoint")
    @CheckStudyHost
    @DeleteMapping("/studies/{studyId}")
    public ResponseEntity<Void> close(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId
    ) {
        studyService.close(studyId);
        return ResponseEntity.noContent().build();
    }
}
