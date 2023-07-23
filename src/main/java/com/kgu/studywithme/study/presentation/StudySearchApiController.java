package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.dto.ResponseWrapper;
import com.kgu.studywithme.study.application.dto.StudyPagingResponse;
import com.kgu.studywithme.study.application.usecase.query.QueryStudyByCategoryUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryStudyByRecommendUseCase;
import com.kgu.studywithme.study.presentation.dto.request.QueryStudyByCategoryRequest;
import com.kgu.studywithme.study.presentation.dto.request.QueryStudyByRecommendRequest;
import com.kgu.studywithme.study.utils.QueryStudyByCategoryCondition;
import com.kgu.studywithme.study.utils.QueryStudyByRecommendCondition;
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
    private final QueryStudyByCategoryUseCase queryStudyByCategoryUseCase;
    private final QueryStudyByRecommendUseCase queryStudyByRecommendUseCase;

    @Operation(summary = "카테고리 기반 스터디 조회 EndPoint")
    @GetMapping
    public ResponseEntity<ResponseWrapper<StudyPagingResponse>> queryStudyByCategory(
            @ModelAttribute @Valid final QueryStudyByCategoryRequest request
    ) {
        final QueryStudyByCategoryCondition condition = new QueryStudyByCategoryCondition(
                request.category(),
                request.sort(),
                request.type(),
                request.province(),
                request.city()
        );
        final StudyPagingResponse response = queryStudyByCategoryUseCase.queryStudyByCategory(
                new QueryStudyByCategoryUseCase.Query(
                        condition,
                        getDefaultPageRequest(request.page())
                )
        );

        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "사용자 관심사 기반 스터디 조회 EndPoint")
    @GetMapping("/recommend")
    public ResponseEntity<ResponseWrapper<StudyPagingResponse>> queryStudyByRecommend(
            @ExtractPayload final Long memberId,
            @ModelAttribute @Valid final QueryStudyByRecommendRequest request
    ) {
        final QueryStudyByRecommendCondition condition = new QueryStudyByRecommendCondition(
                memberId,
                request.sort(),
                request.type(),
                request.province(),
                request.city()
        );
        final StudyPagingResponse response = queryStudyByRecommendUseCase.queryStudyByRecommend(
                new QueryStudyByRecommendUseCase.Query(
                        condition,
                        getDefaultPageRequest(request.page())
                )
        );

        return ResponseEntity.ok(ResponseWrapper.from(response));
    }
}
