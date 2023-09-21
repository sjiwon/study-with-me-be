package com.kgu.studywithme.common.config;

import com.kgu.studywithme.global.aop.CheckAuthUserAspect;
import com.kgu.studywithme.global.aop.CheckStudyHostAspect;
import com.kgu.studywithme.global.aop.CheckStudyParticipantAspect;
import com.kgu.studywithme.global.logging.LoggingStatusManager;
import com.kgu.studywithme.global.logging.LoggingTracer;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class TestAopConfiguration {
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;

    @Bean
    public CheckAuthUserAspect checkAuthUserAspect() {
        return new CheckAuthUserAspect();
    }

    @Bean
    public CheckStudyHostAspect checkStudyHostAspect() {
        return new CheckStudyHostAspect(studyRepository);
    }

    @Bean
    public CheckStudyParticipantAspect checkStudyParticipantAspect() {
        return new CheckStudyParticipantAspect(studyParticipantRepository);
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
