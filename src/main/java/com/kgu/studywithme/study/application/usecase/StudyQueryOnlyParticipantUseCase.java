package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.study.application.usecase.query.GetApplicantById;
import com.kgu.studywithme.study.application.usecase.query.GetAttendanceById;
import com.kgu.studywithme.study.application.usecase.query.GetNoticeById;
import com.kgu.studywithme.study.application.usecase.query.GetWeeklyById;
import com.kgu.studywithme.study.domain.repository.query.StudyInformationRepository;
import com.kgu.studywithme.study.domain.repository.query.dto.AttendanceInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.NoticeInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyApplicantInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.WeeklyInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyQueryOnlyParticipantUseCase {
    private final StudyInformationRepository studyInformationRepository;

    public List<StudyApplicantInformation> getApplicantById(final GetApplicantById query) {
        return studyInformationRepository.fetchApplicantById(query.studyId());
    }

    public List<NoticeInformation> getNoticeById(final GetNoticeById query) {
        return studyInformationRepository.fetchNoticeById(query.studyId());
    }

    public List<AttendanceInformation> getAttendanceById(final GetAttendanceById query) {
        return studyInformationRepository.fetchAttendanceById(query.studyId());
    }

    public List<WeeklyInformation> getWeeklyById(final GetWeeklyById query) {
        return studyInformationRepository.fetchWeeklyById(query.studyId());
    }
}
