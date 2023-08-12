package com.kgu.studywithme.studynotice.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.presentation.dto.request.UpdateStudyNoticeRequest;
import com.kgu.studywithme.studynotice.presentation.dto.request.WriteStudyNoticeRequest;
import com.kgu.studywithme.studynotice.presentation.dto.response.StudyNoticeIdResponse;
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

@Tag(name = "4-6. 스터디 공지사항 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyNoticeApiController {
    private final WriteStudyNoticeUseCase writeStudyNoticeUseCase;
    private final UpdateStudyNoticeUseCase updateStudyNoticeUseCase;
    private final DeleteStudyNoticeUseCase deleteStudyNoticeUseCase;

    @Operation(summary = "스터디 공지사항 작성 EndPoint")
    @CheckStudyHost
    @PostMapping("/notice")
    public ResponseEntity<StudyNoticeIdResponse> write(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @RequestBody @Valid final WriteStudyNoticeRequest request
    ) {
        final Long noticeId = writeStudyNoticeUseCase.writeNotice(
                new WriteStudyNoticeUseCase.Command(
                        hostId,
                        studyId,
                        request.title(),
                        request.content()
                )
        );
        return ResponseEntity.ok(new StudyNoticeIdResponse(noticeId));
    }

    @Operation(summary = "스터디 공지사항 수정 EndPoint")
    @CheckStudyHost
    @PatchMapping("/notices/{noticeId}")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Long noticeId,
            @RequestBody @Valid final UpdateStudyNoticeRequest request
    ) {
        updateStudyNoticeUseCase.updateNotice(
                new UpdateStudyNoticeUseCase.Command(
                        hostId,
                        noticeId,
                        request.title(),
                        request.content()
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 공지사항 삭제 EndPoint")
    @CheckStudyHost
    @DeleteMapping("/notices/{noticeId}")
    public ResponseEntity<Void> delete(
            @ExtractPayload final Long hostId,
            @PathVariable final Long studyId,
            @PathVariable final Long noticeId
    ) {
        deleteStudyNoticeUseCase.deleteNotice(
                new DeleteStudyNoticeUseCase.Command(hostId, noticeId)
        );
        return ResponseEntity.noContent().build();
    }
}
