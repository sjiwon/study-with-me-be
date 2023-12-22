package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class UpdateStudyNoticeUseCase {
    private final StudyNoticeRepository studyNoticeRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final UpdateStudyNoticeCommand command) {
        final StudyNotice studyNotice = studyNoticeRepository.getById(command.noticeId());
        studyNotice.updateNoticeInformation(command.title(), command.content());
    }
}
