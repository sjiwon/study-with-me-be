package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
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
public class WeeklyCreator {
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;

    @StudyWithMeWritableTransactional
    public StudyWeekly invoke(
            final CreateStudyWeeklyCommand command,
            final List<UploadAttachment> attachments,
            final int nextWeek
    ) {
        final Study study = studyRepository.getInProgressStudy(command.studyId());
        final Member creator = memberRepository.getById(command.creatorId());

        final StudyWeekly weekly = createWeekly(command, study, creator, attachments, nextWeek);
        applyParticipantAttendanceStatusFromGeneratedWeekly(weekly.getStudy(), weekly.getWeek());
        return weekly;
    }

    private StudyWeekly createWeekly(
            final CreateStudyWeeklyCommand command,
            final Study study,
            final Member creator,
            final List<UploadAttachment> attachments,
            final int nextWeek
    ) {
        if (command.assignmentExists()) {
            return studyWeeklyRepository.save(StudyWeekly.createWeeklyWithAssignment(
                    study,
                    creator,
                    command.title(),
                    command.content(),
                    nextWeek,
                    command.period(),
                    command.autoAttendance(),
                    attachments
            ));
        }

        return studyWeeklyRepository.save(StudyWeekly.createWeekly(
                study,
                creator,
                command.title(),
                command.content(),
                nextWeek,
                command.period(),
                attachments
        ));
    }

    private void applyParticipantAttendanceStatusFromGeneratedWeekly(final Study study, final int week) {
        final List<StudyAttendance> participantsAttendance = new ArrayList<>();

        final List<Member> approveParticipants = studyParticipantRepository.findParticipantsByStatus(study.getId(), APPROVE);
        approveParticipants.forEach(participant ->
                participantsAttendance.add(StudyAttendance.recordAttendance(
                        study,
                        participant,
                        week,
                        NON_ATTENDANCE
                ))
        );

        log.info("Study[{} - {}] Weekly 생성 -> {} NON_ATTENDANCE", study.getId(), week, participantsAttendance);
        studyAttendanceRepository.saveAll(participantsAttendance);
    }
}
