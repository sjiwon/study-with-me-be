package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.application.usecase.command.UpdateStudyCommand;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.domain.service.StudyResourceValidator;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.ON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Study -> UpdateStudyUseCase 테스트")
class UpdateStudyUseCaseTest extends UseCaseTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final StudyResourceValidator studyResourceValidator = new StudyResourceValidator(studyRepository);
    private final UpdateStudyUseCase sut = new UpdateStudyUseCase(studyResourceValidator, studyRepository);

    private final Member host = JIWON.toMember().apply(1L);

    private Study study;
    private UpdateStudyCommand command;

    @BeforeEach
    void setUp() {
        study = SPRING.toStudy(host.getId()).apply(1L);
        command = new UpdateStudyCommand(
                study.getId(),
                JPA.getName(),
                JPA.getDescription(),
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
        given(studyRepository.isNameUsedByOther(command.studyId(), command.name().getValue())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).isNameUsedByOther(command.studyId(), command.name().getValue()),
                () -> verify(studyRepository, times(0)).getById(command.studyId())
        );
    }

    @Test
    @DisplayName("현재 참여자 수보다 낮게 스터디 정원을 수정할 수 없다")
    void throwExceptionByCapacityCannotCoverCurrentParticipants() {
        // given
        given(studyRepository.isNameUsedByOther(command.studyId(), command.name().getValue())).willReturn(false);
        given(studyRepository.getById(command.studyId())).willReturn(study);
        ReflectionTestUtils.setField(study, "participants", command.capacity() + 1);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.CAPACITY_CANNOT_COVER_CURRENT_PARTICIPANTS.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).isNameUsedByOther(command.studyId(), command.name().getValue()),
                () -> verify(studyRepository, times(1)).getById(command.studyId())
        );
    }

    @Test
    @DisplayName("스터디를 수정한다")
    void success() {
        // given
        given(studyRepository.isNameUsedByOther(command.studyId(), command.name().getValue())).willReturn(false);
        given(studyRepository.getById(command.studyId())).willReturn(study);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).isNameUsedByOther(command.studyId(), command.name().getValue()),
                () -> verify(studyRepository, times(1)).getById(command.studyId()),
                () -> assertThat(study.getName().getValue()).isEqualTo(command.name().getValue()),
                () -> assertThat(study.getDescription().getValue()).isEqualTo(command.description().getValue()),
                () -> assertThat(study.getCapacity().getValue()).isEqualTo(command.capacity()),
                () -> assertThat(study.getParticipants()).isEqualTo(1), // host
                () -> assertThat(study.getCategory()).isEqualTo(command.category()),
                () -> assertThat(study.getThumbnail().getImageName()).isEqualTo(command.thumbnail().getImageName()),
                () -> assertThat(study.getThumbnail().getBackground()).isEqualTo(command.thumbnail().getBackground()),
                () -> assertThat(study.getType()).isEqualTo(command.type()),
                () -> assertThat(study.getLocation()).isNull(),
                () -> assertThat(study.getRecruitmentStatus()).isEqualTo(ON),
                () -> assertThat(study.getGraduationPolicy().getMinimumAttendance()).isEqualTo(command.minimumAttendanceForGraduation()),
                () -> assertThat(study.getHashtags()).containsExactlyInAnyOrderElementsOf(command.hashtags())
        );
    }
}
