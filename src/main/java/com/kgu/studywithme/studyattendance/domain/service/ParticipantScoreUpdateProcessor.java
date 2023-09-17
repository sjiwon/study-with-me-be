package com.kgu.studywithme.studyattendance.domain.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;

@Component
@RequiredArgsConstructor
public class ParticipantScoreUpdateProcessor {
    private final MemberRepository memberRepository;

    @StudyWithMeWritableTransactional
    public void updateByAttendanceStatus(
            final Long participantId,
            final AttendanceStatus previousStatus,
            final AttendanceStatus currentStatus
    ) {
        final Member participant = memberRepository.getById(participantId);

        if (previousStatus == NON_ATTENDANCE) {
            participant.applyScoreByAttendanceStatus(currentStatus);
        } else if (isStatusChanged(previousStatus, currentStatus)) {
            participant.applyScoreByAttendanceStatus(previousStatus, currentStatus);
        }
    }

    private boolean isStatusChanged(final AttendanceStatus previousStatus, final AttendanceStatus currentStatus) {
        return previousStatus != currentStatus;
    }

}
