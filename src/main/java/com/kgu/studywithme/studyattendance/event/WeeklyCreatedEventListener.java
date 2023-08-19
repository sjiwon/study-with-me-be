package com.kgu.studywithme.studyattendance.event;

import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.infrastructure.persistence.StudyAttendanceJpaRepository;
import com.kgu.studywithme.studyparticipant.infrastructure.persistence.StudyParticipantJpaRepository;
import com.kgu.studywithme.studyweekly.event.WeeklyCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;

@Component
@RequiredArgsConstructor
public class WeeklyCreatedEventListener {
    private final StudyParticipantJpaRepository studyParticipantJpaRepository;
    private final StudyAttendanceJpaRepository studyAttendanceJpaRepository;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createWeeklyAndApplyAttendanceStatusToPaticipant(final WeeklyCreatedEvent event) {
        final List<StudyAttendance> participantsAttendance = new ArrayList<>();
        final List<Long> approveParticipantsIds = studyParticipantJpaRepository.findParticipantIdsByStatus(event.studyId(), APPROVE);
        approveParticipantsIds.forEach(
                studyParticipantId -> participantsAttendance.add(
                        StudyAttendance.recordAttendance(
                                event.studyId(),
                                studyParticipantId,
                                event.week(),
                                NON_ATTENDANCE
                        )
                )
        );
        studyAttendanceJpaRepository.saveAll(participantsAttendance);
    }
}
