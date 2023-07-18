package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.participant.ParticipantRepository;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.domain.StudyNoticeRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class WriteStudyNoticeCommentService implements WriteStudyNoticeCommentUseCase {
    private final StudyNoticeRepository studyNoticeRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public void writeNoticeComment(final Command command) {
        final StudyNotice notice = findById(command.noticeId());
        validateWriterIsStudyParticipant(notice.getStudyId(), command.writerId());

        notice.addComment(command.writerId(), command.content());
    }

    private StudyNotice findById(final Long noticeId) {
        return studyNoticeRepository.findById(noticeId)
                .orElseThrow(() -> StudyWithMeException.type(StudyNoticeErrorCode.NOTICE_NOT_FOUND));
    }

    private void validateWriterIsStudyParticipant(final Long studyId, final Long writerId) {
        if (!participantRepository.isParticipant(studyId, writerId)) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_PARTICIPANT_CAN_WRITE_COMMENT);
        }
    }
}
