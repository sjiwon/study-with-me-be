package com.kgu.studywithme.study.controller.week;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.global.aop.CheckStudyParticipant;
import com.kgu.studywithme.study.controller.dto.request.StudyWeeklyRequest;
import com.kgu.studywithme.study.controller.dto.request.WeeklyAssignmentSubmitRequest;
import com.kgu.studywithme.study.service.week.StudyWeeklyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "4-8. 스터디 주차별 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyWeeklyApiController {
    private final StudyWeeklyService studyWeeklyService;

    @Operation(summary = "스터디 주차 등록 EndPoint")
    @CheckStudyHost
    @PostMapping(value = "/week", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createWeek(@ExtractPayload Long hostId,
                                           @PathVariable Long studyId,
                                           @ModelAttribute @Valid StudyWeeklyRequest request) {
        studyWeeklyService.createWeek(studyId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 주차 수정 EndPoint")
    @CheckStudyHost
    @PostMapping(value = "/weeks/{week}", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateWeek(@ExtractPayload Long hostId,
                                           @PathVariable Long studyId,
                                           @PathVariable Integer week,
                                           @ModelAttribute @Valid StudyWeeklyRequest request) {
        studyWeeklyService.updateWeek(studyId, week, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 주차 삭제 EndPoint")
    @CheckStudyHost
    @DeleteMapping("/weeks/{week}")
    public ResponseEntity<Void> deleteWeek(@ExtractPayload Long hostId,
                                           @PathVariable Long studyId,
                                           @PathVariable Integer week) {
        studyWeeklyService.deleteWeek(studyId, week);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 주차별 과제 제출 EndPoint")
    @CheckStudyParticipant
    @PostMapping(value = "/weeks/{week}/assignment", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> submitAssignment(@ExtractPayload Long memberId,
                                                 @PathVariable Long studyId,
                                                 @PathVariable Integer week,
                                                 @ModelAttribute @Valid WeeklyAssignmentSubmitRequest request) {
        studyWeeklyService.submitAssignment(memberId, studyId, week, request.type(), request.file(), request.link());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 주차별 제출한 과제 수정 EndPoint")
    @CheckStudyParticipant
    @PostMapping(value = "/weeks/{week}/assignment/edit", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> editSubmittedAssignment(@ExtractPayload Long memberId,
                                                        @PathVariable Long studyId,
                                                        @PathVariable Integer week,
                                                        @ModelAttribute @Valid WeeklyAssignmentSubmitRequest request) {
        studyWeeklyService.editSubmittedAssignment(memberId, studyId, week, request.type(), request.file(), request.link());
        return ResponseEntity.noContent().build();
    }
}
