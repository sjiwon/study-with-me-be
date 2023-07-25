package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.domain.StudyNoticeRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyNotice -> DeleteStudyNoticeService 테스트")
class DeleteStudyNoticeServiceTest extends UseCaseTest {
    @InjectMocks
    private DeleteStudyNoticeService deleteStudyNoticeService;

    @Mock
    private StudyNoticeRepository studyNoticeRepository;

    private final Member writer = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member anonymous = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final StudyNotice notice = StudyNotice.writeNotice(
            1L,
            writer.getId(),
            "Title",
            "Content"
    ).apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("공지사항 작성자가 아닌 사람이 삭제를 시도하면 예외가 발생한다")
    void throwExceptionByHostIsNotNoticeWriter() {
        // given
        given(studyNoticeRepository.findById(any())).willReturn(Optional.of(notice));

        // when - then
        assertThatThrownBy(() -> deleteStudyNoticeService.deleteNotice(
                new DeleteStudyNoticeUseCase.Command(
                        anonymous.getId(),
                        notice.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyNoticeErrorCode.ONLY_WRITER_CAN_DELETE_NOTICE.getMessage());

        assertAll(
                () -> verify(studyNoticeRepository, times(1)).findById(any()),
                () -> verify(studyNoticeRepository, times(0)).deleteNotice(any())
        );
    }

    @Test
    @DisplayName("공지사항을 삭제한다")
    void success() {
        // given
        given(studyNoticeRepository.findById(any())).willReturn(Optional.of(notice));

        // when
        deleteStudyNoticeService.deleteNotice(
                new DeleteStudyNoticeUseCase.Command(
                        writer.getId(),
                        notice.getId()
                )
        );

        // then
        assertAll(
                () -> verify(studyNoticeRepository, times(1)).findById(any()),
                () -> verify(studyNoticeRepository, times(1)).deleteNotice(any())
        );
    }
}
