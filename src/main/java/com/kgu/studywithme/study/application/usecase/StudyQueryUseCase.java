package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.study.application.usecase.query.GetBasicInformationById;
import com.kgu.studywithme.study.application.usecase.query.GetParticipantById;
import com.kgu.studywithme.study.application.usecase.query.GetReviewById;
import com.kgu.studywithme.study.domain.repository.query.StudyInformationRepository;
import com.kgu.studywithme.study.domain.repository.query.dto.ReviewInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyBasicInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyParticipantInformation;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class StudyQueryUseCase {
    private final StudyInformationRepository studyInformationRepository;

    public StudyBasicInformation getBasicInformationById(final GetBasicInformationById query) {
        return studyInformationRepository.fetchBasicInformationById(query.studyId());
    }

    public ReviewInformation getReviewById(final GetReviewById query) {
        return studyInformationRepository.fetchReviewById(query.studyId());
    }

    public StudyParticipantInformation getParticipantById(final GetParticipantById query) {
        return studyInformationRepository.fetchParticipantById(query.studyId());
    }
}
