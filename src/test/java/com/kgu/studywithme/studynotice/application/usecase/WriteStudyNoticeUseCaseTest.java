package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyNotice -> WriteStudyNoticeUseCase 테스트")
class WriteStudyNoticeUseCaseTest extends UseCaseTest {
    private final StudyNoticeRepository studyNoticeRepository = mock(StudyNoticeRepository.class);
    private final WriteStudyNoticeUseCase sut = new WriteStudyNoticeUseCase(studyNoticeRepository);

    private final WriteStudyNoticeCommand command = new WriteStudyNoticeCommand(1L, 1L, "Notice", "Hello");

    @Test
    @DisplayName("스터디 공지사항을 작성한다")
    void success() {
        // given
        final StudyNotice notice = StudyNotice.writeNotice(command.studyId(), command.hostId(), command.title(), command.content()).apply(1L);
        given(studyNoticeRepository.save(any())).willReturn(notice);

        // when
        final Long noticeId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyNoticeRepository, times(1)).save(any()),
                () -> assertThat(noticeId).isEqualTo(notice.getId())
        );
    }
}
