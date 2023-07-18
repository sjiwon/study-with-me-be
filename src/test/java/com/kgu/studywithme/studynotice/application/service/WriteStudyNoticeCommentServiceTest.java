package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.participant.ParticipantRepository;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.domain.StudyNoticeRepository;
import com.kgu.studywithme.studynotice.domain.comment.StudyNoticeComment;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyNotice/Comment -> WriteStudyNoticeCommentService 테스트")
class WriteStudyNoticeCommentServiceTest extends UseCaseTest {
    @InjectMocks
    private WriteStudyNoticeCommentService writeStudyNoticeCommentService;

    @Mock
    private StudyNoticeRepository studyNoticeRepository;

    @Mock
    private ParticipantRepository participantRepository;

    private final Member writer = JIWON.toMember().apply(1L, LocalDateTime.now());

    private final WriteStudyNoticeCommentUseCase.Command command =
            new WriteStudyNoticeCommentUseCase.Command(
                    1L,
                    writer.getId(),
                    "댓글!!"
            );

    @Test
    @DisplayName("스터디 참여자(status = APPROVE)가 아니면 공지사항에 댓글을 작성할 수 없다")
    void throwExceptionByWriterIsNotStudyParticipant() {
        // given
        final StudyNotice notice = StudyNotice.writeNotice(
                1L,
                writer.getId(),
                "공지사항 제목",
                "공지사항 내용"
        ).apply(1L, LocalDateTime.now());
        given(studyNoticeRepository.findById(any())).willReturn(Optional.of(notice));
        given(participantRepository.isParticipant(any(), any())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> writeStudyNoticeCommentService.writeNoticeComment(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyNoticeErrorCode.ONLY_PARTICIPANT_CAN_WRITE_COMMENT.getMessage());

        verify(studyNoticeRepository, times(1)).findById(any());
        verify(participantRepository, times(1)).isParticipant(any(), any());
    }

    @Test
    @DisplayName("공지사항에 댓글을 작성한다")
    void success() {
        // given
        final StudyNotice notice = StudyNotice.writeNotice(
                1L,
                writer.getId(),
                "공지사항 제목",
                "공지사항 내용"
        ).apply(1L, LocalDateTime.now());
        given(studyNoticeRepository.findById(any())).willReturn(Optional.of(notice));
        given(participantRepository.isParticipant(any(), any())).willReturn(true);

        // when
        writeStudyNoticeCommentService.writeNoticeComment(command);

        // then
        verify(studyNoticeRepository, times(1)).findById(any());
        verify(participantRepository, times(1)).isParticipant(any(), any());
        assertAll(
                () -> assertThat(notice.getComments()).hasSize(1),
                () -> assertThat(
                        notice.getComments()
                                .stream()
                                .map(StudyNoticeComment::getWriterId)
                                .toList()
                ).containsExactlyInAnyOrder(writer.getId()),
                () -> assertThat(
                        notice.getComments()
                                .stream()
                                .map(StudyNoticeComment::getContent)
                                .toList()
                ).containsExactlyInAnyOrder("댓글!!")
        );
    }
}
