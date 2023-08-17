package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.study.application.usecase.query.QueryApplicantByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryAttendanceByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryNoticeByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryWeeklyByIdUseCase;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infrastructure.query.dto.AttendanceInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.NoticeInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyApplicantInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.WeeklyInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyQueryOnlyParticipantService implements
        QueryApplicantByIdUseCase,
        QueryNoticeByIdUseCase,
        QueryAttendanceByIdUseCase,
        QueryWeeklyByIdUseCase {

    private final StudyRepository studyRepository;

    @Override
    public List<StudyApplicantInformation> invoke(final QueryApplicantByIdUseCase.Query query) {
        return studyRepository.fetchApplicantById(query.studyId());
    }

    @Override
    public List<NoticeInformation> invoke(final QueryNoticeByIdUseCase.Query query) {
        return studyRepository.fetchNoticeById(query.studyId());
    }

    @Override
    public List<AttendanceInformation> invoke(final QueryAttendanceByIdUseCase.Query query) {
        return studyRepository.fetchAttendanceById(query.studyId());
    }

    @Override
    public List<WeeklyInformation> invoke(final QueryWeeklyByIdUseCase.Query query) {
        return studyRepository.fetchWeeklyById(query.studyId());
    }
}
