package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.domain.comment.StudyNoticeComment;
import com.kgu.studywithme.studynotice.domain.comment.StudyNoticeCommentRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyNotice/Comment -> UpdateStudyNoticeCommentService 테스트")
class UpdateStudyNoticeCommentServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdateStudyNoticeCommentService updateStudyNoticeCommentService;

    @Mock
    private StudyNoticeCommentRepository studyNoticeCommentRepository;

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
    @DisplayName("댓글 작성자가 아니면 수정할 수 없다")
    void throwExceptionByMemberIsNotWriter() {
        // given
        given(studyNoticeCommentRepository.findById(any())).willReturn(Optional.of(comment));

        // when - then
        assertThatThrownBy(() -> updateStudyNoticeCommentService.invoke(
                new UpdateStudyNoticeCommentUseCase.Command(
                        comment.getId(),
                        anonymous.getId(),
                        "댓글 수정"
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyNoticeErrorCode.ONLY_WRITER_CAN_UPDATE_NOTICE_COMMENT.getMessage());

        verify(studyNoticeCommentRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("공지사항 댓글을 수정한다")
    void success() {
        // given
        given(studyNoticeCommentRepository.findById(any())).willReturn(Optional.of(comment));

        // when
        updateStudyNoticeCommentService.invoke(
                new UpdateStudyNoticeCommentUseCase.Command(
                        comment.getId(),
                        writer.getId(),
                        "댓글 수정"
                )
        );

        // then
        assertAll(
                () -> verify(studyNoticeCommentRepository, times(1)).findById(any()),
                () -> assertThat(comment.getContent()).isEqualTo("댓글 수정")
        );
    }
}
