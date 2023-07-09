package com.kgu.studywithme.global.aop;

import com.kgu.studywithme.study.application.StudyValidator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CheckStudyParticipantAspect {
    private final StudyValidator studyValidator;

    @Before("@annotation(com.kgu.studywithme.global.aop.CheckStudyParticipant) && args(memberId, studyId, ..)")
    public void checkParticipant(
            final Long studyId,
            final Long memberId
    ) {
        studyValidator.validateStudyParticipant(studyId, memberId);
    }
}
