package com.kgu.studywithme.global.scheduler;

import com.kgu.studywithme.studyattendance.domain.service.UpdateWeeklyAttendanceBatchProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class UpdateWeeklyAttendanceScheduler {
    private final StringRedisTemplate redisTemplate;
    private final UpdateWeeklyAttendanceBatchProcessor updateWeeklyAttendanceBatchProcessor;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void processAbsenceCheckScheduler() {
        if (canExecute()) {
            updateWeeklyAttendanceBatchProcessor.checkAbsenceParticipantAndApplyAbsenceScore();
        }
    }

    private boolean canExecute() {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("scheduling", "on", Duration.ofMinutes(5)));
    }
}
