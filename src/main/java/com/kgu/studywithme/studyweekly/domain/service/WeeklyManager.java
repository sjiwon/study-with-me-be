package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.domain.model.Period;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyAttachmentRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklySubmitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyManager {
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final StudyWeeklyAttachmentRepository studyWeeklyAttachmentRepository;
    private final StudyWeeklySubmitRepository studyWeeklySubmitRepository;

    @StudyWithMeWritableTransactional
    public StudyWeekly saveWeekly(final StudyWeekly target) {
        final StudyWeekly weekly = studyWeeklyRepository.save(target);
        applyParticipantAttendanceStatusFromGeneratedWeekly(weekly.getStudyId(), weekly.getWeek());
        return weekly;
    }

    private void applyParticipantAttendanceStatusFromGeneratedWeekly(final Long studyId, final int week) {
        final List<StudyAttendance> participantsAttendance = new ArrayList<>();
        final List<Long> approveParticipantsIds = studyParticipantRepository.findParticipantIdsByStatus(studyId, APPROVE);
        approveParticipantsIds.forEach(studyParticipantId ->
                participantsAttendance.add(StudyAttendance.recordAttendance(
                        studyId,
                        studyParticipantId,
                        week,
                        NON_ATTENDANCE
                ))
        );

        log.info("Study[{} - {}] Weekly 생성 -> {} NON_ATTENDANCE", studyId, week, participantsAttendance);
        studyAttendanceRepository.saveAll(participantsAttendance);
    }

    @StudyWithMeWritableTransactional
    public void updateWeekly(
            final Long weeklyId,
            final String title,
            final String content,
            final Period period,
            final boolean assignmentExists,
            final boolean autoAttendance,
            final List<UploadAttachment> attachments
    ) {
        final StudyWeekly weekly = studyWeeklyRepository.getById(weeklyId);
        weekly.update(
                title,
                content,
                period,
                assignmentExists,
                autoAttendance,
                attachments
        );
    }

    @StudyWithMeWritableTransactional
    public void deleteWeekly(final StudyWeekly weekly) {
        // 1. 해당 주차 출석 정보 삭제
        studyAttendanceRepository.deleteFromSpecificWeekly(weekly.getStudyId(), weekly.getWeek());

        // 2. 제출한 과제 삭제
        studyWeeklySubmitRepository.deleteFromSpecificWeekly(weekly.getId());

        // 3. 해당 주차 첨부파일 삭제
        studyWeeklyAttachmentRepository.deleteFromSpecificWeekly(weekly.getId());

        // 4. 해당 주차 삭제
        studyWeeklyRepository.deleteById(weekly.getId());
    }
}
