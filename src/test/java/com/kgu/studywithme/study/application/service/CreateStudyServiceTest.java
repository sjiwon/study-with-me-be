package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberReadAdapter;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.usecase.command.CreateStudyUseCase;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.OS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Study -> CreateStudyService 테스트")
class CreateStudyServiceTest extends UseCaseTest {
    @InjectMocks
    private CreateStudyService createStudyService;

    @Mock
    private MemberReadAdapter memberReadAdapter;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyParticipantRepository studyParticipantRepository;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final CreateStudyUseCase.Command command = new CreateStudyUseCase.Command(
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
        given(studyRepository.isNameExists(any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> createStudyService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).isNameExists(any()),
                () -> verify(memberReadAdapter, times(0)).getById(any()),
                () -> verify(studyRepository, times(0)).save(any()),
                () -> verify(studyParticipantRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("스터디를 생성한다")
    void success() {
        // given
        given(studyRepository.isNameExists(any())).willReturn(false);
        given(memberReadAdapter.getById(any())).willReturn(host);

        final Study study = OS.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
        given(studyRepository.save(any())).willReturn(study);

        // when
        final Long savedStudyId = createStudyService.invoke(command);

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).isNameExists(any()),
                () -> verify(memberReadAdapter, times(1)).getById(any()),
                () -> verify(studyRepository, times(1)).save(any()),
                () -> verify(studyParticipantRepository, times(1)).save(any()),
                () -> assertThat(savedStudyId).isEqualTo(study.getId())
        );
    }
}
