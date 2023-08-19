package com.kgu.studywithme.global.aop;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantVerificationRepositoryAdapter;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CheckStudyParticipantAspect {
    private final ParticipantVerificationRepositoryAdapter participantVerificationRepositoryAdapter;

    @Before("@annotation(com.kgu.studywithme.global.aop.CheckStudyParticipant) && args(memberId, studyId, ..)")
    public void checkParticipant(
            final Long studyId,
            final Long memberId
    ) {
        if (!participantVerificationRepositoryAdapter.isParticipant(studyId, memberId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.MEMBER_IS_NOT_PARTICIPANT);
        }
    }
}
