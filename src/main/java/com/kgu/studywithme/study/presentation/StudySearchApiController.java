package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.auth.domain.model.Authenticated;
import com.kgu.studywithme.global.annotation.Auth;
import com.kgu.studywithme.study.application.usecase.StudySearchUseCase;
import com.kgu.studywithme.study.application.usecase.dto.StudyPagingResponse;
import com.kgu.studywithme.study.application.usecase.query.GetStudiesByCategory;
import com.kgu.studywithme.study.application.usecase.query.GetStudiesByRecommend;
import com.kgu.studywithme.study.presentation.dto.request.GetStudiesByCategoryRequest;
import com.kgu.studywithme.study.presentation.dto.request.GetStudiesByRecommendRequest;
import com.kgu.studywithme.study.utils.search.SearchByCategoryCondition;
import com.kgu.studywithme.study.utils.search.SearchByRecommendCondition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4-2. 메인페이지 스터디 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies")
public class StudySearchApiController {
    private final StudySearchUseCase studySearchUseCase;

    @Operation(summary = "카테고리 기반 스터디 조회 EndPoint")
    @GetMapping
    public ResponseEntity<StudyPagingResponse> getStudiesByCategory(
            @ModelAttribute @Valid final GetStudiesByCategoryRequest request
    ) {
        final SearchByCategoryCondition condition = new SearchByCategoryCondition(
                request.category(),
                request.sort(),
                request.type(),
                request.province(),
                request.city()
        );
        final StudyPagingResponse response = studySearchUseCase.getStudiesByCategory(new GetStudiesByCategory(condition, request.page()));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 관심사 기반 스터디 조회 EndPoint")
    @GetMapping("/recommend")
    public ResponseEntity<StudyPagingResponse> getStudiesByRecommend(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final GetStudiesByRecommendRequest request
    ) {
        final SearchByRecommendCondition condition = new SearchByRecommendCondition(
                authenticated.id(),
                request.sort(),
                request.type(),
                request.province(),
                request.city()
        );
        final StudyPagingResponse response = studySearchUseCase.getStudiesByRecommend(new GetStudiesByRecommend(condition, request.page()));
        return ResponseEntity.ok(response);
    }
}
