package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.application.adapter.StudyDuplicateCheckRepositoryAdapter;
import com.kgu.studywithme.study.application.usecase.command.UpdateStudyUseCase;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Study -> UpdateStudyService 테스트")
class UpdateStudyServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdateStudyService updateStudyService;

    @Mock
    private StudyDuplicateCheckRepositoryAdapter studyDuplicateCheckRepositoryAdapter;

    @Mock
    private StudyRepository studyRepository;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private Study study;
    private UpdateStudyUseCase.Command command;

    @BeforeEach
    void setUp() {
        study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
        command = new UpdateStudyUseCase.Command(
                study.getId(),
                JPA.getName().getValue(),
                JPA.getDescription().getValue(),
                JPA.getCategory(),
                JPA.getCapacity().getValue(),
                JPA.getThumbnail(),
                JPA.getType(),
                null,
                null,
                true,
                JPA.getMinimumAttendanceForGraduation(),
                JPA.getHashtags()
        );
    }

    @Test
    @DisplayName("다른 스터디가 사용하고 있는 이름으로 수정할 수 없다")
    void throwExceptionByDuplicateName() {
        // given
        given(studyDuplicateCheckRepositoryAdapter.isNameUsedByOther(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> updateStudyService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());

        assertAll(
                () -> verify(studyDuplicateCheckRepositoryAdapter, times(1)).isNameUsedByOther(any(), any()),
                () -> verify(studyRepository, times(0)).getById(any())
        );
    }

    @Test
    @DisplayName("현재 참여자 수보다 낮게 스터디 정원을 수정할 수 없다")
    void throwExceptionByCapacityCannotCoverCurrentParticipants() {
        // given
        given(studyDuplicateCheckRepositoryAdapter.isNameUsedByOther(any(), any())).willReturn(false);
        given(studyRepository.getById(any())).willReturn(study);

        final int capacity = study.getCapacity().getValue();
        for (int i = 0; i < capacity - 1; i++) { // make full
            study.addParticipant();
        }

        // when - then
        assertThatThrownBy(() -> updateStudyService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.CAPACITY_CANNOT_COVER_CURRENT_PARTICIPANTS.getMessage());

        assertAll(
                () -> verify(studyDuplicateCheckRepositoryAdapter, times(1)).isNameUsedByOther(any(), any()),
                () -> verify(studyRepository, times(1)).getById(any())
        );
    }

    @Test
    @DisplayName("스터디를 수정한다")
    void success() {
        // given
        given(studyDuplicateCheckRepositoryAdapter.isNameUsedByOther(any(), any())).willReturn(false);
        given(studyRepository.getById(any())).willReturn(study);

        // when
        updateStudyService.invoke(command);

        // then
        assertAll(
                () -> verify(studyDuplicateCheckRepositoryAdapter, times(1)).isNameUsedByOther(any(), any()),
                () -> verify(studyRepository, times(1)).getById(any()),
                () -> assertThat(study.getName()).isEqualTo(JPA.getName()),
                () -> assertThat(study.getDescription()).isEqualTo(JPA.getDescription()),
                () -> assertThat(study.getCapacity().getValue()).isEqualTo(JPA.getCapacity().getValue()),
                () -> assertThat(study.getParticipants()).isEqualTo(1), // host
                () -> assertThat(study.getCategory()).isEqualTo(JPA.getCategory()),
                () -> assertThat(study.getThumbnail()).isEqualTo(JPA.getThumbnail()),
                () -> assertThat(study.getType()).isEqualTo(JPA.getType()),
                () -> assertThat(study.getLocation()).isNull(),
                () -> assertThat(study.getRecruitmentStatus()).isEqualTo(IN_PROGRESS),
                () -> assertThat(study.getGraduationPolicy().getMinimumAttendance()).isEqualTo(JPA.getMinimumAttendanceForGraduation()),
                () -> assertThat(study.getHashtags()).containsExactlyInAnyOrderElementsOf(JPA.getHashtags())
        );
    }
}
