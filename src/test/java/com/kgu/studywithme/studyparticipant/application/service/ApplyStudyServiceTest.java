package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.service.QueryStudyByIdService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyStudyUseCase;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> ApplyStudyService 테스트")
class ApplyStudyServiceTest extends UseCaseTest {
    @InjectMocks
    private ApplyStudyService applyStudyService;

    @Mock
    private QueryStudyByIdService queryStudyByIdService;

    @Mock
    private StudyParticipantRepository studyParticipantRepository;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member applier = GHOST.toMember().apply(2L, LocalDateTime.now());
    private Study study;

    @BeforeEach
    void setUp() {
        study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    }

    @Test
    @DisplayName("스터디가 모집중이지 않으면 참여 신청을 할 수 없다")
    void throwExceptionByStudyIsNotRecruitingNow() {
        // given
        study.recruitingEnd();
        given(queryStudyByIdService.findById(any())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> applyStudyService.apply(
                new ApplyStudyUseCase.Command(
                        study.getId(),
                        applier.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.STUDY_IS_NOT_RECRUITING_NOW.getMessage());

        verify(queryStudyByIdService, times(1)).findById(any());
        verify(studyParticipantRepository, times(0))
                .isApplierOrParticipant(any(), any());
        verify(studyParticipantRepository, times(0))
                .isAlreadyLeaveOrGraduatedParticipant(any(), any());
        verify(studyParticipantRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("스터디 팀장은 본인 스터디에 참여 신청을 할 수 없다")
    void throwExceptionByStudyHostCannotApplyOwnStudy() {
        // given
        given(queryStudyByIdService.findById(any())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> applyStudyService.apply(
                new ApplyStudyUseCase.Command(
                        study.getId(),
                        host.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.STUDY_HOST_CANNOT_APPLY.getMessage());

        verify(queryStudyByIdService, times(1)).findById(any());
        verify(studyParticipantRepository, times(0))
                .isApplierOrParticipant(any(), any());
        verify(studyParticipantRepository, times(0))
                .isAlreadyLeaveOrGraduatedParticipant(any(), any());
        verify(studyParticipantRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("이미 신청했거나 참여중인 스터디에 다시 참여 신청할 수 없다")
    void throwExceptionByAlreadyApplyOrParticipate() {
        // given
        given(queryStudyByIdService.findById(any())).willReturn(study);
        given(studyParticipantRepository.isApplierOrParticipant(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> applyStudyService.apply(
                new ApplyStudyUseCase.Command(
                        study.getId(),
                        applier.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.ALREADY_APPLY_OR_PARTICIPATE.getMessage());

        verify(queryStudyByIdService, times(1)).findById(any());
        verify(studyParticipantRepository, times(1))
                .isApplierOrParticipant(any(), any());
        verify(studyParticipantRepository, times(0))
                .isAlreadyLeaveOrGraduatedParticipant(any(), any());
        verify(studyParticipantRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("스터디 참여를 취소했거나 졸업한 사람은 동일 스터디에 다시 참여 신청을 할 수 없다")
    void throwExceptionByAlreadyLeaveOrGraduated() {
        // given
        given(queryStudyByIdService.findById(any())).willReturn(study);
        given(studyParticipantRepository.isApplierOrParticipant(any(), any())).willReturn(false);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(any(), any()))
                .willReturn(true);

        // when - then
        assertThatThrownBy(() -> applyStudyService.apply(
                new ApplyStudyUseCase.Command(
                        study.getId(),
                        applier.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.ALREADY_LEAVE_OR_GRADUATED.getMessage());

        verify(queryStudyByIdService, times(1)).findById(any());
        verify(studyParticipantRepository, times(1))
                .isApplierOrParticipant(any(), any());
        verify(studyParticipantRepository, times(1))
                .isAlreadyLeaveOrGraduatedParticipant(any(), any());
        verify(studyParticipantRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("스터디 참여 신청에 성공한다")
    void success() {
        // given
        given(queryStudyByIdService.findById(any())).willReturn(study);
        given(studyParticipantRepository.isApplierOrParticipant(any(), any())).willReturn(false);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(any(), any()))
                .willReturn(false);

        // when
        applyStudyService.apply(
                new ApplyStudyUseCase.Command(
                        study.getId(),
                        applier.getId()
                )
        );

        // then
        verify(queryStudyByIdService, times(1)).findById(any());
        verify(studyParticipantRepository, times(1))
                .isApplierOrParticipant(any(), any());
        verify(studyParticipantRepository, times(1))
                .isAlreadyLeaveOrGraduatedParticipant(any(), any());
        verify(studyParticipantRepository, times(1)).save(any());
    }
}