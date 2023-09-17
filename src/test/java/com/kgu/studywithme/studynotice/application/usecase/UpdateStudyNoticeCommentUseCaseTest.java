package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.domain.model.StudyNoticeComment;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeCommentRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyNotice/Comment -> UpdateStudyNoticeCommentUseCase 테스트")
class UpdateStudyNoticeCommentUseCaseTest extends UseCaseTest {
    private final StudyNoticeCommentRepository studyNoticeCommentRepository = mock(StudyNoticeCommentRepository.class);
    private final UpdateStudyNoticeCommentUseCase sut = new UpdateStudyNoticeCommentUseCase(studyNoticeCommentRepository);

    private final Member writer = JIWON.toMember().apply(1L);
    private final Member anonymous = JIWON.toMember().apply(2L);
    private final StudyNotice notice = StudyNotice.writeNotice(1L, writer.getId(), "제목", "내용").apply(1L);
    private final StudyNoticeComment comment = StudyNoticeComment.writeComment(notice, writer.getId(), "댓글!!").apply(1L);

    @Test
    @DisplayName("댓글 작성자가 아니면 수정할 수 없다")
    void throwExceptionByMemberIsNotWriter() {
        // given
        final UpdateStudyNoticeCommentCommand command = new UpdateStudyNoticeCommentCommand(comment.getId(), anonymous.getId(), "댓글 수정");
        given(studyNoticeCommentRepository.getById(command.commentId())).willReturn(comment);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyNoticeErrorCode.ONLY_WRITER_CAN_UPDATE_NOTICE_COMMENT.getMessage());

        verify(studyNoticeCommentRepository, times(1)).getById(command.commentId());
    }

    @Test
    @DisplayName("공지사항 댓글을 수정한다")
    void success() {
        // given
        final UpdateStudyNoticeCommentCommand command = new UpdateStudyNoticeCommentCommand(comment.getId(), writer.getId(), "댓글 수정");
        given(studyNoticeCommentRepository.getById(command.commentId())).willReturn(comment);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyNoticeCommentRepository, times(1)).getById(any()),
                () -> assertThat(comment.getContent()).isEqualTo("댓글 수정")
        );
    }
}
