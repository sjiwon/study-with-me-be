package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.domain.StudyNoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyNotice -> WriteStudyNoticeService 테스트")
class WriteStudyNoticeServiceTest extends UseCaseTest {
    @InjectMocks
    private WriteStudyNoticeService writeStudyNoticeService;

    @Mock
    private StudyNoticeRepository studyNoticeRepository;

    private final WriteStudyNoticeUseCase.Command command =
            new WriteStudyNoticeUseCase.Command(1L, 1L, "Notice 1", "Hello World");

    @Test
    @DisplayName("스터디 공지사항을 작성한다")
    void success() {
        // given
        final StudyNotice notice = StudyNotice.writeNotice(
                command.studyId(),
                command.hostId(),
                command.title(),
                command.content()
        ).apply(1L, LocalDateTime.now());
        given(studyNoticeRepository.save(any())).willReturn(notice);

        // when
        final Long noticeId = writeStudyNoticeService.writeNotice(command);

        // then
        assertAll(
                () -> verify(studyNoticeRepository, times(1)).save(any()),
                () -> assertThat(noticeId).isEqualTo(notice.getId())
        );
    }
}
