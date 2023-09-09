package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.study.application.adapter.StudyCategorySearchRepositoryAdapter;
import com.kgu.studywithme.study.application.service.dto.StudyPagingResponse;
import com.kgu.studywithme.study.application.usecase.query.QueryStudyByCategoryUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryStudyByRecommendUseCase;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudySearchService implements
        QueryStudyByCategoryUseCase,
        QueryStudyByRecommendUseCase {

    private final StudyCategorySearchRepositoryAdapter studyCategorySearchRepositoryAdapter;

    @Override
    public StudyPagingResponse invoke(final QueryStudyByCategoryUseCase.Query query) {
        final Slice<StudyPreview> result
                = studyCategorySearchRepositoryAdapter.fetchStudyByCategory(query.condition(), query.pageable());
        return new StudyPagingResponse(result.getContent(), result.hasNext());
    }

    @Override
    public StudyPagingResponse invoke(final QueryStudyByRecommendUseCase.Query query) {
        final Slice<StudyPreview> result
                = studyCategorySearchRepositoryAdapter.fetchStudyByRecommend(query.condition(), query.pageable());
        return new StudyPagingResponse(result.getContent(), result.hasNext());
    }
}
