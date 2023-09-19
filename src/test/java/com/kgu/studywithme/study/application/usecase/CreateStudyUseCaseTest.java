package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.application.usecase.command.CreateStudyCommand;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.domain.service.StudyResourceValidator;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.OS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Study -> CreateStudyUseCase 테스트")
class CreateStudyUseCaseTest extends UseCaseTest {
    private final StudyResourceValidator studyResourceValidator = mock(StudyResourceValidator.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final CreateStudyUseCase sut = new CreateStudyUseCase(studyResourceValidator, memberRepository, studyRepository, studyParticipantRepository);

    private final Member host = JIWON.toMember().apply(1L);
    private final CreateStudyCommand command = new CreateStudyCommand(
            host.getId(),
            OS.getName(),
            OS.getDescription(),
            OS.getCategory(),
            OS.getCapacity(),
            OS.getThumbnail(),
            OS.getType(),
            null,
            null,
            OS.getMinimumAttendanceForGraduation(),
            OS.getHashtags()
    );

    @Test
    @DisplayName("이미 사용하고 있는 이름이면 스터디 생성에 실패한다")
    void throwExceptionByDuplicateName() {
        // given
        doThrow(StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME))
                .when(studyResourceValidator)
                .validateInCreate(command.name());

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());

        assertAll(
                () -> verify(studyResourceValidator, times(1)).validateInCreate(command.name()),
                () -> verify(memberRepository, times(0)).getById(command.hostId()),
                () -> verify(studyRepository, times(0)).save(any()),
                () -> verify(studyParticipantRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("스터디를 생성한다")
    void success() {
        // given
        doNothing()
                .when(studyResourceValidator)
                .validateInCreate(command.name());
        given(memberRepository.getById(command.hostId())).willReturn(host);

        final Study study = command.toDomain().apply(1L);
        given(studyRepository.save(any())).willReturn(study);

        // when
        final Long savedStudyId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyResourceValidator, times(1)).validateInCreate(command.name()),
                () -> verify(memberRepository, times(1)).getById(command.hostId()),
                () -> verify(studyRepository, times(1)).save(any()),
                () -> verify(studyParticipantRepository, times(1)).save(any()),
                () -> assertThat(savedStudyId).isEqualTo(study.getId())
        );
    }
}
