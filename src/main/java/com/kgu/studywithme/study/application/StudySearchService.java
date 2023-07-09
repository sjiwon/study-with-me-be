package com.kgu.studywithme.study.application;

import com.kgu.studywithme.study.application.dto.response.DefaultStudyResponse;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudySearchService {
    private final StudyRepository studyRepository;

    public DefaultStudyResponse findStudyByCategory(
            final StudyCategoryCondition condition,
            final Pageable pageable
    ) {
        final Slice<BasicStudy> result = studyRepository.findStudyByCategory(condition, pageable);
        return new DefaultStudyResponse(result.getContent(), result.hasNext());
    }

    public DefaultStudyResponse findStudyByRecommend(
            final StudyRecommendCondition condition,
            final Pageable pageable
    ) {
        final Slice<BasicStudy> result = studyRepository.findStudyByRecommend(condition, pageable);
        return new DefaultStudyResponse(result.getContent(), result.hasNext());
    }
}
