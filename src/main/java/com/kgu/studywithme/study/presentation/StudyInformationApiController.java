package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.study.application.usecase.StudyQueryUseCase;
import com.kgu.studywithme.study.application.usecase.query.GetBasicInformationById;
import com.kgu.studywithme.study.application.usecase.query.GetParticipantById;
import com.kgu.studywithme.study.application.usecase.query.GetReviewById;
import com.kgu.studywithme.study.domain.repository.query.dto.ReviewInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyBasicInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyParticipantInformation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4-3-1. 스터디 정보 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}")
public class StudyInformationApiController {
    private final StudyQueryUseCase studyQueryUseCase;

    @Operation(summary = "스터디 기본 정보 조회 EndPoint")
    @GetMapping
    public ResponseEntity<StudyBasicInformation> getBasicInformationById(@PathVariable final Long studyId) {
        final StudyBasicInformation response = studyQueryUseCase.getBasicInformationById(new GetBasicInformationById(studyId));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스터디 리뷰 조회 EndPoint")
    @GetMapping("/reviews")
    public ResponseEntity<ReviewInformation> getReviewById(@PathVariable final Long studyId) {
        final ReviewInformation response = studyQueryUseCase.getReviewById(new GetReviewById(studyId));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스터디 참여자 조회 EndPoint")
    @GetMapping("/participants")
    public ResponseEntity<StudyParticipantInformation> getParticipantById(@PathVariable final Long studyId) {
        final StudyParticipantInformation response = studyQueryUseCase.getParticipantById(new GetParticipantById(studyId));
        return ResponseEntity.ok(response);
    }
}
