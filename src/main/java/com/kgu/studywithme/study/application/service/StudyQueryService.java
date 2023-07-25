package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.study.application.usecase.query.QueryBasicInformationByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryParticipantByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryReviewByIdUseCase;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.ReviewInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyBasicInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyParticipantInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyQueryService implements
        QueryBasicInformationByIdUseCase,
        QueryReviewByIdUseCase,
        QueryParticipantByIdUseCase {

    private final StudyRepository studyRepository;

    @Override
    public StudyBasicInformation queryBasicInformation(final QueryBasicInformationByIdUseCase.Query query) {
        return studyRepository.fetchBasicInformationById(query.studyId());
    }

    @Override
    public ReviewInformation queryReview(final QueryReviewByIdUseCase.Query query) {
        return studyRepository.fetchReviewById(query.studyId());
    }

    @Override
    public StudyParticipantInformation queryParticipant(final QueryParticipantByIdUseCase.Query query) {
        return studyRepository.fetchParticipantById(query.studyId());
    }
}
