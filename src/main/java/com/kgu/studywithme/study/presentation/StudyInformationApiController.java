package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.global.dto.ResponseWrapper;
import com.kgu.studywithme.study.application.usecase.query.QueryBasicInformationByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryParticipantByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryReviewByIdUseCase;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.ReviewInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyBasicInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyParticipantInformation;
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
    private final QueryBasicInformationByIdUseCase queryBasicInformationByIdUseCase;
    private final QueryReviewByIdUseCase queryReviewByIdUseCase;
    private final QueryParticipantByIdUseCase queryParticipantByIdUseCase;

    @Operation(summary = "스터디 기본 정보 조회 EndPoint")
    @GetMapping
    public ResponseEntity<ResponseWrapper<StudyBasicInformation>> getInformation(@PathVariable final Long studyId) {
        final StudyBasicInformation response = queryBasicInformationByIdUseCase.queryBasicInformation(
                new QueryBasicInformationByIdUseCase.Query(studyId)
        );
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "스터디 리뷰 조회 EndPoint")
    @GetMapping("/reviews")
    public ResponseEntity<ResponseWrapper<ReviewInformation>> getReviews(@PathVariable final Long studyId) {
        final ReviewInformation response = queryReviewByIdUseCase.queryReview(
                new QueryReviewByIdUseCase.Query(studyId)
        );
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "스터디 참여자 조회 EndPoint")
    @GetMapping("/participants")
    public ResponseEntity<ResponseWrapper<StudyParticipantInformation>> getApproveParticipants(@PathVariable final Long studyId) {
        final StudyParticipantInformation response = queryParticipantByIdUseCase.queryParticipant(
                new QueryParticipantByIdUseCase.Query(studyId)
        );
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }
}
