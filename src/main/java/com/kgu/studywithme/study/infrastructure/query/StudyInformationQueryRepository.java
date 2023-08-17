package com.kgu.studywithme.study.infrastructure.query;

import com.kgu.studywithme.study.infrastructure.query.dto.AttendanceInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.NoticeInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.ReviewInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyApplicantInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyBasicInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyParticipantInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.WeeklyInformation;

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
