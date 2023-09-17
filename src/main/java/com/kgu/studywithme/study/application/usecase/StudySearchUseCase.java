package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.study.application.usecase.dto.StudyPagingResponse;
import com.kgu.studywithme.study.application.usecase.query.GetStudiesByCategory;
import com.kgu.studywithme.study.application.usecase.query.GetStudiesByRecommend;
import com.kgu.studywithme.study.domain.repository.query.StudyCategorySearchRepository;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudySearchUseCase {
    private final StudyCategorySearchRepository studyCategorySearchRepository;

    public StudyPagingResponse getStudiesByCategory(final GetStudiesByCategory query) {
        final Slice<StudyPreview> result = studyCategorySearchRepository.fetchStudyByCategory(query.condition(), query.pageable());
        return new StudyPagingResponse(result.getContent(), result.hasNext());
    }

    public StudyPagingResponse getStudiesByRecommend(final GetStudiesByRecommend query) {
        final Slice<StudyPreview> result = studyCategorySearchRepository.fetchStudyByRecommend(query.condition(), query.pageable());
        return new StudyPagingResponse(result.getContent(), result.hasNext());
    }
}
