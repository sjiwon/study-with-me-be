package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.study.application.StudySearchService;
import com.kgu.studywithme.study.application.dto.response.DefaultStudyResponse;
import com.kgu.studywithme.study.presentation.dto.request.StudyCategorySearchRequest;
import com.kgu.studywithme.study.presentation.dto.request.StudyRecommendSearchRequest;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kgu.studywithme.study.utils.PagingConstants.getDefaultPageRequest;

@Tag(name = "4-2. 메인페이지 스터디 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies")
public class StudySearchApiController {
    private final StudySearchService studySearchService;

    @Operation(summary = "카테고리 기반 스터디 조회 EndPoint")
    @GetMapping
    public ResponseEntity<DefaultStudyResponse> findStudyByCategory(
            @ModelAttribute @Valid final StudyCategorySearchRequest request
    ) {
        final StudyCategoryCondition condition = new StudyCategoryCondition(request);
        final DefaultStudyResponse result = studySearchService.findStudyByCategory(condition, getDefaultPageRequest(request.page()));

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "사용자 관심사 기반 스터디 조회 EndPoint")
    @GetMapping("/recommend")
    public ResponseEntity<DefaultStudyResponse> findStudyByRecommend(
            @ExtractPayload final Long memberId,
            @ModelAttribute @Valid final StudyRecommendSearchRequest request
    ) {
        final StudyRecommendCondition condition = new StudyRecommendCondition(memberId, request);
        final DefaultStudyResponse result = studySearchService.findStudyByRecommend(condition, getDefaultPageRequest(request.page()));

        return ResponseEntity.ok(result);
    }
}