package com.kgu.studywithme.study.infrastructure.repository.query;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.*;

import java.util.List;

public interface StudyInformationQueryRepository {
    StudyBasicInformation fetchBasicInformationById(final Long studyId);

    ReviewInformation fetchReviewById(final Long studyId);

    StudyParticipantInformation fetchParticipantById(final Long studyId);

    List<StudyApplicantInformation> fetchApplicantById(final Long studyId);

    List<NoticeInformation> fetchNoticeById(final Long studyId);

    List<AttendanceInformation> fetchAttendanceById(final Long studyId);

    List<WeeklyInformation> fetchWeeklyById(final Long studyId);
}
