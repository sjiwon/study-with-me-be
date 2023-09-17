package com.kgu.studywithme.studyattendance.event;

import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.event.WeeklyCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyCreatedEventListener {
    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createWeeklyAndApplyAttendanceStatusToPaticipant(final WeeklyCreatedEvent event) {
        log.info("주차 생성에 따른 해당 주차 참여자 출석정보 추가 (NON_ATTENDANCE) -> {}", event);

        final List<StudyAttendance> participantsAttendance = new ArrayList<>();
        final List<Long> approveParticipantsIds = studyParticipantRepository.findParticipantIdsByStatus(event.studyId(), APPROVE);
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
        studyAttendanceRepository.saveAll(participantsAttendance);
    }
}
