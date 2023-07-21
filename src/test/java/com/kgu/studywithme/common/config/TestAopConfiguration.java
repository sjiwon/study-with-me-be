package com.kgu.studywithme.common.config;

import com.kgu.studywithme.global.aop.CheckMemberIdentityAspect;
import com.kgu.studywithme.global.aop.CheckStudyHostAspect;
import com.kgu.studywithme.global.aop.CheckStudyParticipantAspect;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
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
    public CheckMemberIdentityAspect checkMemberIdentityAspect() {
        return new CheckMemberIdentityAspect();
    }

    @Bean
    public CheckStudyHostAspect checkStudyHostAspect() {
        return new CheckStudyHostAspect(studyRepository);
    }

    @Bean
    public CheckStudyParticipantAspect checkStudyParticipantAspect() {
        return new CheckStudyParticipantAspect(studyParticipantRepository);
    }
}
