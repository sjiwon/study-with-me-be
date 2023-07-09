package com.kgu.studywithme.study.application;

import com.kgu.studywithme.study.application.dto.response.*;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.AttendanceInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.NoticeInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.ReviewInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.StudyApplicantInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyInformationService {
    private final StudyFindService studyFindService;
    private final StudyRepository studyRepository;

    public StudyInformation getInformation(final Long studyId) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        return new StudyInformation(study);
    }

    public ReviewAssembler getReviews(final Long studyId) {
        final int graduateCount = studyRepository.getGraduatedParticipantCountByStudyId(studyId);
        final List<ReviewInformation> reviews = studyRepository.findReviewByStudyId(studyId);

        return new ReviewAssembler(graduateCount, reviews);
    }

    public NoticeAssembler getNotices(final Long studyId) {
        final List<NoticeInformation> result = studyRepository.findNoticeWithCommentsByStudyId(studyId);
        return new NoticeAssembler(result);
    }

    public StudyApplicant getApplicants(final Long studyId) {
        final List<StudyApplicantInformation> result = studyRepository.findApplicantByStudyId(studyId);
        return new StudyApplicant(result);
    }

    public StudyParticipant getApproveParticipants(final Long studyId) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final StudyMember host = new StudyMember(study.getHost());
        final List<StudyMember> participants = study.getApproveParticipantsWithoutHost()
                .stream()
                .map(StudyMember::new)
                .toList();

        return new StudyParticipant(host, participants);
    }

    public AttendanceAssmbler getAttendances(final Long studyId) {
        final List<AttendanceInformation> result = studyRepository.findAttendanceByStudyId(studyId);
        final List<StudyMemberAttendanceResult> attendanceResults = result.stream()
                .collect(Collectors.groupingBy(AttendanceInformation::getParticipant))
                .entrySet().stream()
                .map(entry ->
                        new StudyMemberAttendanceResult(
                                entry.getKey(),
                                entry.getValue()
                                        .stream()
                                        .map(info ->
                                                new AttendanceSummary(
                                                        info.getWeek(),
                                                        info.getAttendanceStatus()
                                                )
                                        )
                                        .toList()
                        )
                )
                .toList();

        return new AttendanceAssmbler(attendanceResults);
    }

    public WeeklyAssembler getWeeks(final Long studyId) {
        final List<Week> weeks = studyRepository.findWeeklyByStudyId(studyId);
        final List<WeeklySummary> result = weeks.stream()
                .map(WeeklySummary::new)
                .toList();

        return new WeeklyAssembler(result);
    }
}
