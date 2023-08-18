package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.study.application.adapter.StudyCategoryQueryRepositoryAdapter;
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

    private final StudyCategoryQueryRepositoryAdapter studyCategoryQueryRepositoryAdapter;

    @Override
    public StudyPagingResponse invoke(final QueryStudyByCategoryUseCase.Query query) {
        final Slice<StudyPreview> result = studyCategoryQueryRepositoryAdapter.fetchStudyByCategory(query.condition(), query.pageable());
        return new StudyPagingResponse(result.getContent(), result.hasNext());
    }

    @Override
    public StudyPagingResponse invoke(final QueryStudyByRecommendUseCase.Query query) {
        final Slice<StudyPreview> result = studyCategoryQueryRepositoryAdapter.fetchStudyByRecommend(query.condition(), query.pageable());
        return new StudyPagingResponse(result.getContent(), result.hasNext());
    }
}
