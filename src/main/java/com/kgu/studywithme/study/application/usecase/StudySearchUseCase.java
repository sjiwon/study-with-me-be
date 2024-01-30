package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.global.query.SliceResponse;
import com.kgu.studywithme.study.application.usecase.query.GetStudiesByCategory;
import com.kgu.studywithme.study.application.usecase.query.GetStudiesByRecommend;
import com.kgu.studywithme.study.domain.repository.query.StudyCategorySearchRepository;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

import static com.kgu.studywithme.global.query.PageCreator.query;

@UseCase
@RequiredArgsConstructor
public class StudySearchUseCase {
    private final StudyCategorySearchRepository studyCategorySearchRepository;

    public SliceResponse<List<StudyPreview>> getStudiesByCategory(final GetStudiesByCategory query) {
        final Slice<StudyPreview> result = studyCategorySearchRepository.fetchStudyByCategory(query.condition(), query(query.page()));
        return new SliceResponse<>(result.getContent(), result.hasNext());
    }

    public SliceResponse<List<StudyPreview>> getStudiesByRecommend(final GetStudiesByRecommend query) {
        final Slice<StudyPreview> result = studyCategorySearchRepository.fetchStudyByRecommend(query.condition(), query(query.page()));
        return new SliceResponse<>(result.getContent(), result.hasNext());
    }
}
