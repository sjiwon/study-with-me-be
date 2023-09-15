package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.global.aop.CheckAuthUser;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.study.application.usecase.command.CreateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.command.TerminateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.command.UpdateStudyUseCase;
import com.kgu.studywithme.study.domain.model.Capacity;
import com.kgu.studywithme.study.domain.model.Description;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.model.StudyThumbnail;
import com.kgu.studywithme.study.domain.model.StudyType;
import com.kgu.studywithme.study.presentation.dto.request.CreateStudyRequest;
import com.kgu.studywithme.study.presentation.dto.request.UpdateStudyRequest;
import com.kgu.studywithme.study.presentation.dto.response.StudyIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    @CheckAuthUser
    @PostMapping("/study")
    public ResponseEntity<StudyIdResponse> create(
            @ExtractPayload final Long hostId,
            @RequestBody @Valid final CreateStudyRequest request
    ) {
        final Long savedStudyId = createStudyUseCase.invoke(
                new CreateStudyUseCase.Command(
                        hostId,
                        new StudyName(request.name()),
                        new Description(request.description()),
                        Category.from(request.category()),
                        new Capacity(request.capacity()),
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
                .body(new StudyIdResponse(savedStudyId));
    }

    @Operation(summary = "스터디 수정 EndPoint")
    @CheckStudyHost
    @PatchMapping("/studies/{studyId}")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @RequestBody @Valid final UpdateStudyRequest request
    ) {
        updateStudyUseCase.invoke(
                new UpdateStudyUseCase.Command(
                        studyId,
                        request.name(),
                        request.description(),
                        Category.from(request.category()),
                        request.capacity(),
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
        terminateStudyUseCase.invoke(new TerminateStudyUseCase.Command(studyId));
        return ResponseEntity.noContent().build();
    }
}
