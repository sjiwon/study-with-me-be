package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.infrastructure.persistence.StudyNoticeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WriteStudyNoticeService implements WriteStudyNoticeUseCase {
    private final StudyNoticeJpaRepository studyNoticeJpaRepository;

    @Override
    public Long invoke(final Command command) {
        final StudyNotice notice = StudyNotice.writeNotice(
                command.studyId(),
                command.hostId(),
                command.title(),
                command.content()
        );

        return studyNoticeJpaRepository.save(notice).getId();
    }
}
