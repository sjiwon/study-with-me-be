package com.kgu.studywithme.studyattendance.event;

import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.infrastructure.persistence.StudyAttendanceJpaRepository;
import com.kgu.studywithme.studyparticipant.infrastructure.persistence.StudyParticipantJpaRepository;
import com.kgu.studywithme.studyweekly.event.WeeklyCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyCreatedEventListener {
    private final StudyParticipantJpaRepository studyParticipantJpaRepository;
    private final StudyAttendanceJpaRepository studyAttendanceJpaRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createWeeklyAndApplyAttendanceStatusToPaticipant(final WeeklyCreatedEvent event) {
        log.info("주차 생성에 따른 해당 주차 참여자 출석정보 추가 (NON_ATTENDANCE) -> {}", event);

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
