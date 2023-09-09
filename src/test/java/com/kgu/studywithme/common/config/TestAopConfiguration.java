package com.kgu.studywithme.common.config;

import com.kgu.studywithme.global.aop.CheckAuthUserAspect;
import com.kgu.studywithme.global.aop.CheckStudyHostAspect;
import com.kgu.studywithme.global.aop.CheckStudyParticipantAspect;
import com.kgu.studywithme.global.logging.LoggingStatusManager;
import com.kgu.studywithme.global.logging.LoggingTracer;
import com.kgu.studywithme.study.application.adapter.StudyVerificationRepositoryAdapter;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantVerificationRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class TestAopConfiguration {
    private final StudyVerificationRepositoryAdapter studyVerificationRepositoryAdapter;
    private final ParticipantVerificationRepositoryAdapter participantVerificationRepositoryAdapter;

    @Bean
    public CheckAuthUserAspect checkAuthUserAspect() {
        return new CheckAuthUserAspect();
    }

    @Bean
    public CheckStudyHostAspect checkStudyHostAspect() {
        return new CheckStudyHostAspect(studyVerificationRepositoryAdapter);
    }

    @Bean
    public CheckStudyParticipantAspect checkStudyParticipantAspect() {
        return new CheckStudyParticipantAspect(participantVerificationRepositoryAdapter);
    }

    @Bean
    public LoggingStatusManager loggingStatusManager() {
        return new LoggingStatusManager();
    }

    @Bean
    public LoggingTracer loggingTracer() {
        return new LoggingTracer(loggingStatusManager());
    }
}
