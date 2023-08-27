package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.domain.StudyNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WriteStudyNoticeService implements WriteStudyNoticeUseCase {
    private final StudyNoticeRepository studyNoticeRepository;

    @Override
    public Long invoke(final Command command) {
        final StudyNotice notice = StudyNotice.writeNotice(
                command.studyId(),
                command.hostId(),
                command.title(),
                command.content()
        );

        return studyNoticeRepository.save(notice).getId();
    }
}
