package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WriteStudyNoticeCommentUseCase {
    private final StudyNoticeRepository studyNoticeRepository;
    private final MemberRepository memberRepository;
    private final StudyParticipantRepository studyParticipantRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final WriteStudyNoticeCommentCommand command) {
        final StudyNotice notice = studyNoticeRepository.getById(command.noticeId());
        final Member writer = memberRepository.getById(command.writerId());

        validateWriterIsStudyParticipant(notice.getStudy(), writer);
        notice.addComment(writer, command.content());
    }

    private void validateWriterIsStudyParticipant(final Study study, final Member writer) {
        if (!studyParticipantRepository.isParticipant(study.getId(), writer.getId())) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_PARTICIPANT_CAN_WRITE_COMMENT);
        }
    }
}
