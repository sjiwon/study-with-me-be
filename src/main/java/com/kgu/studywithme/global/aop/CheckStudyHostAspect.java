package com.kgu.studywithme.global.aop;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CheckStudyHostAspect {
    private final StudyRepository studyRepository;

    @Before("@annotation(com.kgu.studywithme.global.aop.CheckStudyHost) && args(hostId, studyId, ..)")
    public void checkStudyHost(final Long hostId, final Long studyId) {
        final Long realHostId = studyRepository.getHostId(studyId);

        if (!realHostId.equals(hostId)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_HOST);
        }
    }
}
