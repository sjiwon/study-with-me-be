package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.study.application.usecase.command.CreateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.command.TerminateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.command.UpdateStudyUseCase;
import com.kgu.studywithme.study.domain.*;
import com.kgu.studywithme.study.presentation.dto.request.CreateStudyRequest;
import com.kgu.studywithme.study.presentation.dto.request.UpdateStudyRequest;
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
    private final CreateStudyUseCase createStudyUseCase;
    private final UpdateStudyUseCase updateStudyUseCase;
    private final TerminateStudyUseCase terminateStudyUseCase;

    @Operation(summary = "스터디 생성 EndPoint")
    @PostMapping("/study")
    public ResponseEntity<Void> create(
            @ExtractPayload final Long hostId,
            @RequestBody @Valid final CreateStudyRequest request
    ) {
        final Long savedStudyId = createStudyUseCase.createStudy(
                new CreateStudyUseCase.Command(
                        hostId,
                        StudyName.from(request.name()),
                        Description.from(request.description()),
                        Category.from(request.category()),
                        Capacity.from(request.capacity()),
                        StudyThumbnail.from(request.thumbnail()),
                        StudyType.from(request.type()),
                        request.province(),
                        request.city(),
                        request.minimumAttendanceForGraduation(),
                        request.hashtags()
                )
        );

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
            @RequestBody @Valid final UpdateStudyRequest request
    ) {
        updateStudyUseCase.updateStudy(
                new UpdateStudyUseCase.Command(
                        studyId,
                        StudyName.from(request.name()),
                        Description.from(request.description()),
                        Category.from(request.category()),
                        Capacity.from(request.capacity()),
                        StudyThumbnail.from(request.thumbnail()),
                        StudyType.from(request.type()),
                        request.province(),
                        request.city(),
                        request.recruitmentStatus(),
                        request.minimumAttendanceForGraduation(),
                        request.hashtags()
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 종료 EndPoint")
    @CheckStudyHost
    @DeleteMapping("/studies/{studyId}")
    public ResponseEntity<Void> terminate(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId
    ) {
        terminateStudyUseCase.terminateStudy(
                new TerminateStudyUseCase.Command(studyId)
        );
        return ResponseEntity.noContent().build();
    }
}
