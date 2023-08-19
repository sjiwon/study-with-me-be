package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.domain.comment.StudyNoticeComment;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import com.kgu.studywithme.studynotice.infrastructure.persistence.comment.StudyNoticeCommentJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyNotice/Comment -> DeleteStudyNoticeCommentService 테스트")
class DeleteStudyNoticeCommentServiceTest extends UseCaseTest {
    @InjectMocks
    private DeleteStudyNoticeCommentService deleteStudyNoticeCommentService;

    @Mock
    private StudyNoticeCommentJpaRepository studyNoticeCommentJpaRepository;

    private final Member writer = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member anonymous = JIWON.toMember().apply(2L, LocalDateTime.now());
    private final StudyNotice notice = StudyNotice.writeNotice(
            1L,
            writer.getId(),
            "공지사항 제목",
            "공지사항 내용"
    ).apply(1L, LocalDateTime.now());
    final StudyNoticeComment comment = StudyNoticeComment.writeComment(
            notice,
            writer.getId(),
            "댓글!!"
    ).apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("댓글 작성자가 아니면 삭제할 수 없다")
    void throwExceptionByMemberIsNotWriter() {
        // given
        given(studyNoticeCommentJpaRepository.findById(any())).willReturn(Optional.of(comment));

        // when - then
        assertThatThrownBy(() -> deleteStudyNoticeCommentService.invoke(
                new DeleteStudyNoticeCommentUseCase.Command(comment.getId(), anonymous.getId())
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyNoticeErrorCode.ONLY_WRITER_CAN_DELETE_NOTICE_COMMENT.getMessage());

        assertAll(
                () -> verify(studyNoticeCommentJpaRepository, times(1)).findById(any()),
                () -> verify(studyNoticeCommentJpaRepository, times(0)).delete(any())
        );
    }

    @Test
    @DisplayName("공지사항 댓글을 삭제한다")
    void success() {
        // given
        given(studyNoticeCommentJpaRepository.findById(any())).willReturn(Optional.of(comment));

        // when
        deleteStudyNoticeCommentService.invoke(new DeleteStudyNoticeCommentUseCase.Command(comment.getId(), writer.getId()));

        // then
        assertAll(
                () -> verify(studyNoticeCommentJpaRepository, times(1)).findById(any()),
                () -> verify(studyNoticeCommentJpaRepository, times(1)).delete(any())
        );
    }
}
