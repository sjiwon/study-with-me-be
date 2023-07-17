package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeUseCase;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyNotice -> UpdateStudyNoticeService 테스트")
class UpdateStudyNoticeServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdateStudyNoticeService updateStudyNoticeService;

    @Mock
    private StudyNoticeRepository studyNoticeRepository;

    private final Member writer = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member anonymous = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final StudyNotice notice = StudyNotice.writeNotice(
            1L,
            writer.getId(),
            "Title",
            "Content"
    );

    @Test
    @DisplayName("공지사항 작성자가 아닌 사람이 수정을 시도하면 예외가 발생한다")
    void throwExceptionByHostIsNotNoticeWriter() {
        // given
        given(studyNoticeRepository.findById(any())).willReturn(Optional.of(notice));

        // when - then
        assertThatThrownBy(() -> updateStudyNoticeService.updateNotice(
                new UpdateStudyNoticeUseCase.Command(
                        anonymous.getId(),
                        notice.getId(),
                        "Title-Update",
                        "Content-Update"
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyNoticeErrorCode.ONLY_WRITER_CAN_UPDATE_NOTICE.getMessage());
        verify(studyNoticeRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("공지사항을 수정한다")
    void success() {
        // given
        given(studyNoticeRepository.findById(any())).willReturn(Optional.of(notice));

        // when
        updateStudyNoticeService.updateNotice(
                new UpdateStudyNoticeUseCase.Command(
                        writer.getId(),
                        notice.getId(),
                        "Title-Update",
                        "Content-Update"
                )
        );

        // then
        verify(studyNoticeRepository, times(1)).findById(any());
        assertAll(
                () -> assertThat(notice.getTitle()).isEqualTo("Title-Update"),
                () -> assertThat(notice.getContent()).isEqualTo("Content-Update")
        );
    }
}
