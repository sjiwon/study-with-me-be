package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.service.QueryStudyByIdService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyparticipant.application.usecase.command.DelegateHostAuthorityUseCase;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> DelegateHostAuthorityService 테스트")
class DelegateHostAuthorityServiceTest extends UseCaseTest {
    @InjectMocks
    private DelegateHostAuthorityService delegateHostAuthorityService;

    @Mock
    private QueryStudyByIdService queryStudyByIdService;

    @Mock
    private StudyParticipantRepository studyParticipantRepository;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member newHost = GHOST.toMember().apply(2L, LocalDateTime.now());
    private Study study;

    @BeforeEach
    void setUp() {
        study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    }

    @Test
    @DisplayName("스터디가 종료되었으면 팀장 권한을 위임할 수 없다")
    void throwExceptionByStudyIsTerminated() {
        // given
        study.terminate();
        given(queryStudyByIdService.findById(any())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> delegateHostAuthorityService.delegateHostAuthority(
                new DelegateHostAuthorityUseCase.Command(
                        study.getId(),
                        newHost.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.STUDY_IS_TERMINATED.getMessage());

        assertAll(
                () -> verify(queryStudyByIdService, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(0)).isParticipant(any(), any())
        );
    }

    @Test
    @DisplayName("팀장 권한을 기존 팀장(Self Invoke)에게 위임할 수 없다")
    void throwExceptionByNewHostIsCurrentHost() {
        // given
        given(queryStudyByIdService.findById(any())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> delegateHostAuthorityService.delegateHostAuthority(
                new DelegateHostAuthorityUseCase.Command(
                        study.getId(),
                        host.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.SELF_DELEGATING_NOT_ALLOWED.getMessage());

        assertAll(
                () -> verify(queryStudyByIdService, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(0)).isParticipant(any(), any())
        );
    }

    @Test
    @DisplayName("스터디 참여자가 아니면 팀장 권한을 위임할 수 없다")
    void throwExceptionByNewHostIsNotParticipant() {
        // given
        given(queryStudyByIdService.findById(any())).willReturn(study);
        given(studyParticipantRepository.isParticipant(any(), any())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> delegateHostAuthorityService.delegateHostAuthority(
                new DelegateHostAuthorityUseCase.Command(
                        study.getId(),
                        newHost.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.NON_PARTICIPANT_CANNOT_BE_HOST.getMessage());

        assertAll(
                () -> verify(queryStudyByIdService, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(1)).isParticipant(any(), any())
        );
    }

    @Test
    @DisplayName("팀장 권한을 위임한다 -> 졸업 요건 수정 기회 초기화")
    void success() {
        // given
        given(queryStudyByIdService.findById(any())).willReturn(study);
        given(studyParticipantRepository.isParticipant(any(), any())).willReturn(true);

        ReflectionTestUtils.setField(study.getGraduationPolicy(), "updateChance", 1);
        assertThat(study.getGraduationPolicy().getUpdateChance()).isEqualTo(1);

        // when
        delegateHostAuthorityService.delegateHostAuthority(
                new DelegateHostAuthorityUseCase.Command(
                        study.getId(),
                        newHost.getId()
                )
        );

        // then
        assertAll(
                () -> verify(queryStudyByIdService, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(1)).isParticipant(any(), any()),
                () -> assertThat(study.getGraduationPolicy().getUpdateChance()).isEqualTo(3) // default chance = 3
        );
    }
}
