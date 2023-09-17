package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeCommand;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateStudyNoticeUseCase {
    private final StudyNoticeRepository studyNoticeRepository;

    public void invoke(final UpdateStudyNoticeCommand command) {
        studyNoticeRepository.update(command.noticeId(), command.title(), command.content());
    }
}
