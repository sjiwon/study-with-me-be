package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeCommand;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeCommentRepository;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteStudyNoticeUseCase {
    private final StudyNoticeCommentRepository studyNoticeCommentRepository;
    private final StudyNoticeRepository studyNoticeRepository;

    public void invoke(final DeleteStudyNoticeCommand command) {
        studyNoticeCommentRepository.deleteByNoticeId(command.noticeId());
        studyNoticeRepository.deleteById(command.noticeId());
    }
}
