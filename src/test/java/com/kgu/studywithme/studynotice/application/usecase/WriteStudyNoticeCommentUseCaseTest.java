package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.domain.model.StudyNoticeComment;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyNotice/Comment -> WriteStudyNoticeCommentUseCase 테스트")
class WriteStudyNoticeCommentUseCaseTest extends UseCaseTest {
    private final StudyNoticeRepository studyNoticeRepository = mock(StudyNoticeRepository.class);
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final WriteStudyNoticeCommentUseCase sut = new WriteStudyNoticeCommentUseCase(studyNoticeRepository, studyParticipantRepository);

    private final Member host = JIWON.toMember().apply(1L);
    private final Study study = SPRING.toStudy(host.getId()).apply(1L);
    private final StudyNotice notice = StudyNotice.writeNotice(study.getId(), host.getId(), "제목", "내용").apply(1L);
    private final WriteStudyNoticeCommentCommand command = new WriteStudyNoticeCommentCommand(notice.getId(), host.getId(), "댓글!!");

    @Test
    @DisplayName("스터디에 참여중인 사용자가 아니면 공지사항에 댓글을 작성할 수 없다")
    void throwExceptionByWriterIsNotStudyParticipant() {
        // given
        given(studyNoticeRepository.getById(command.noticeId())).willReturn(notice);
        given(studyParticipantRepository.isParticipant(notice.getStudyId(), command.writerId())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyNoticeErrorCode.ONLY_PARTICIPANT_CAN_WRITE_COMMENT.getMessage());

        assertAll(
                () -> verify(studyNoticeRepository, times(1)).getById(command.noticeId()),
                () -> verify(studyParticipantRepository, times(1)).isParticipant(notice.getStudyId(), command.writerId())
        );
    }

    @Test
    @DisplayName("공지사항에 댓글을 작성한다")
    void success() {
        // given
        given(studyNoticeRepository.getById(command.noticeId())).willReturn(notice);
        given(studyParticipantRepository.isParticipant(notice.getStudyId(), command.writerId())).willReturn(true);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyNoticeRepository, times(1)).getById(command.noticeId()),
                () -> verify(studyParticipantRepository, times(1)).isParticipant(notice.getStudyId(), command.writerId()),
                () -> assertThat(notice.getComments()).hasSize(1),
                () -> assertThat(notice.getComments())
                        .map(StudyNoticeComment::getWriterId)
                        .containsExactlyInAnyOrder(host.getId()),
                () -> assertThat(notice.getComments())
                        .map(StudyNoticeComment::getContent)
                        .containsExactlyInAnyOrder("댓글!!")
        );
    }
}
