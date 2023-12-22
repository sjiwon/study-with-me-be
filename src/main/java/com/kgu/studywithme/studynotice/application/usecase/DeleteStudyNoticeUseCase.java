package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeCommentRepository;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class DeleteStudyNoticeUseCase {
    private final StudyNoticeCommentRepository studyNoticeCommentRepository;
    private final StudyNoticeRepository studyNoticeRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final DeleteStudyNoticeCommand command) {
        final StudyNotice studyNotice = studyNoticeRepository.getById(command.noticeId());
        studyNoticeCommentRepository.deleteByNoticeId(studyNotice.getId());
        studyNoticeRepository.deleteById(studyNotice.getId());
    }
}
