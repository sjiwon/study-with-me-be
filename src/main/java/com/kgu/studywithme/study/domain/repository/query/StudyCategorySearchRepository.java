package com.kgu.studywithme.study.domain.repository.query;

import com.kgu.studywithme.study.domain.repository.query.dto.StudyPreview;
import com.kgu.studywithme.study.utils.search.SearchByCategoryCondition;
import com.kgu.studywithme.study.utils.search.SearchByRecommendCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StudyCategorySearchRepository {
    Slice<StudyPreview> fetchStudyByCategory(final SearchByCategoryCondition condition, final Pageable pageable);

    Slice<StudyPreview> fetchStudyByRecommend(final SearchByRecommendCondition condition, final Pageable pageable);
}
