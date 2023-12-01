package com.kgu.studywithme.study.domain.repository.query;

import com.kgu.studywithme.study.domain.repository.query.dto.StudyPreview;
import com.kgu.studywithme.study.utils.search.SearchByCategoryCondition;
import com.kgu.studywithme.study.utils.search.SearchByRecommendCondition;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyCategorySearchRepository {
    List<StudyPreview> fetchStudyByCategory(final SearchByCategoryCondition condition, final Pageable pageable);

    List<StudyPreview> fetchStudyByRecommend(final SearchByRecommendCondition condition, final Pageable pageable);
}
