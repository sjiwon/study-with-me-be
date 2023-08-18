package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.study.application.adapter.StudyInformationQueryRepositoryAdapter;
import com.kgu.studywithme.study.application.usecase.query.QueryBasicInformationByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryParticipantByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryReviewByIdUseCase;
import com.kgu.studywithme.study.infrastructure.query.dto.ReviewInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyBasicInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyParticipantInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyQueryService implements
        QueryBasicInformationByIdUseCase,
        QueryReviewByIdUseCase,
        QueryParticipantByIdUseCase {

    private final StudyInformationQueryRepositoryAdapter studyInformationQueryRepositoryAdapter;

    @Override
    public StudyBasicInformation invoke(final QueryBasicInformationByIdUseCase.Query query) {
        return studyInformationQueryRepositoryAdapter.fetchBasicInformationById(query.studyId());
    }

    @Override
    public ReviewInformation invoke(final QueryReviewByIdUseCase.Query query) {
        return studyInformationQueryRepositoryAdapter.fetchReviewById(query.studyId());
    }

    @Override
    public StudyParticipantInformation invoke(final QueryParticipantByIdUseCase.Query query) {
        return studyInformationQueryRepositoryAdapter.fetchParticipantById(query.studyId());
    }
}
