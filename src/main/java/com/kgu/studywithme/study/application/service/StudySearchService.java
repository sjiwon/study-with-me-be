package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.study.application.dto.StudyPagingResponse;
import com.kgu.studywithme.study.application.usecase.query.QueryStudyByCategoryUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryStudyByRecommendUseCase;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudySearchService implements
        QueryStudyByCategoryUseCase,
        QueryStudyByRecommendUseCase {

    private final StudyRepository studyRepository;

    @Override
    public StudyPagingResponse queryStudyByCategory(final QueryStudyByCategoryUseCase.Query query) {
        final Slice<StudyPreview> result = studyRepository.fetchStudyByCategory(query.condition(), query.pageable());
        return new StudyPagingResponse(result.getContent(), result.hasNext());
    }

    @Override
    public StudyPagingResponse queryStudyByRecommend(final QueryStudyByRecommendUseCase.Query query) {
        final Slice<StudyPreview> result = studyRepository.fetchStudyByRecommend(query.condition(), query.pageable());
        return new StudyPagingResponse(result.getContent(), result.hasNext());
    }
}
