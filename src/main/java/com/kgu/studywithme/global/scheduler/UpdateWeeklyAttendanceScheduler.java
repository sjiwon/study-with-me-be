package com.kgu.studywithme.global.scheduler;

import com.kgu.studywithme.studyattendance.domain.service.UpdateWeeklyAttendanceBatchProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateWeeklyAttendanceScheduler {
    private final UpdateWeeklyAttendanceBatchProcessor updateWeeklyAttendanceBatchProcessor;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void processAbsenceCheckScheduler() {
        updateWeeklyAttendanceBatchProcessor.checkAbsenceParticipantAndApplyAbsenceScore();
    }
}
