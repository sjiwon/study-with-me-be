package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.adapter.StudyReadAdapter;
import com.kgu.studywithme.study.application.usecase.command.TerminateStudyUseCase;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Study -> TerminateStudyService 테스트")
class TerminateStudyServiceTest extends UseCaseTest {
    @InjectMocks
    private TerminateStudyService terminateStudyService;

    @Mock
    private StudyReadAdapter studyReadAdapter;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final TerminateStudyUseCase.Command command = new TerminateStudyUseCase.Command(study.getId());

    @Test
    @DisplayName("스터디를 종료시킨다")
    void success() {
        // given
        given(studyReadAdapter.getById(any())).willReturn(study);

        // when
        terminateStudyService.invoke(command);

        // then
        assertAll(
                () -> verify(studyReadAdapter, times(1)).getById(any()),
                () -> assertThat(study.isTerminated()).isTrue()
        );
    }
}
