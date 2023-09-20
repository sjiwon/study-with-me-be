package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.application.usecase.command.TerminateStudyCommand;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Study -> TerminateStudyUseCase 테스트")
class TerminateStudyUseCaseTest extends UseCaseTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final TerminateStudyUseCase sut = new TerminateStudyUseCase(studyRepository);

    private final Member host = JIWON.toMember().apply(1L);
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L);
    private final TerminateStudyCommand command = new TerminateStudyCommand(study.getId());

    @Test
    @DisplayName("스터디를 종료시킨다")
    void success() {
        // given
        given(studyRepository.getById(command.studyId())).willReturn(study);
        assertThat(study.isTerminated()).isFalse();

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getById(command.studyId()),
                () -> assertThat(study.isTerminated()).isTrue()
        );
    }
}
