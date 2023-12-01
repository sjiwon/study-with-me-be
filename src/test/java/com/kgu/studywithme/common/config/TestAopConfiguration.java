package com.kgu.studywithme.common.config;

import com.kgu.studywithme.global.aop.CheckAuthUserAop;
import com.kgu.studywithme.global.aop.CheckStudyHostAop;
import com.kgu.studywithme.global.aop.CheckStudyParticipantAop;
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
    public CheckAuthUserAop checkAuthUserAspect() {
        return new CheckAuthUserAop();
    }

    @Bean
    public CheckStudyHostAop checkStudyHostAspect() {
        return new CheckStudyHostAop(studyRepository);
    }

    @Bean
    public CheckStudyParticipantAop checkStudyParticipantAspect() {
        return new CheckStudyParticipantAop(studyParticipantRepository);
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
