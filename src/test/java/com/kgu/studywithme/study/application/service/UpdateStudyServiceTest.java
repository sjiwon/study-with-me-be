package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.usecase.command.UpdateStudyUseCase;
import com.kgu.studywithme.study.domain.*;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;
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
    private StudyRepository studyRepository;

    @Mock
    private QueryStudyByIdService queryStudyByIdService;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final UpdateStudyUseCase.Command command =
            new UpdateStudyUseCase.Command(
                    study.getId(),
                    StudyName.from(JPA.getName()),
                    Description.from(JPA.getDescription()),
                    JPA.getCategory(),
                    Capacity.from(JPA.getCapacity()),
                    JPA.getThumbnail(),
                    JPA.getType(),
                    null,
                    null,
                    true,
                    JPA.getMinimumAttendanceForGraduation(),
                    JPA.getHashtags()
            );

    @Test
    @DisplayName("다른 스터디가 사용하고 있는 이름으로 수정할 수 없다")
    void throwExceptionByDuplicateName() {
        // given
        given(studyRepository.isNameUsedByOther(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> updateStudyService.updateStudy(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());

        verify(studyRepository, times(1)).isNameUsedByOther(any(), any());
        verify(queryStudyByIdService, times(0)).findById(any());
    }

    @Test
    @DisplayName("스터디를 수정한다")
    void success() {
        // given
        given(studyRepository.isNameUsedByOther(any(), any())).willReturn(false);
        given(queryStudyByIdService.findById(any())).willReturn(study);

        // when
        updateStudyService.updateStudy(command);

        // then
        verify(studyRepository, times(1)).isNameUsedByOther(any(), any());
        verify(queryStudyByIdService, times(1)).findById(any());

        assertAll(
                () -> assertThat(study.getNameValue()).isEqualTo(JPA.getName()),
                () -> assertThat(study.getDescriptionValue()).isEqualTo(JPA.getDescription()),
                () -> assertThat(study.getCapacity()).isEqualTo(JPA.getCapacity()),
                () -> assertThat(study.getCategory()).isEqualTo(JPA.getCategory()),
                () -> assertThat(study.getThumbnail()).isEqualTo(JPA.getThumbnail()),
                () -> assertThat(study.getType()).isEqualTo(JPA.getType()),
                () -> assertThat(study.getLocation()).isNull(),
                () -> assertThat(study.getRecruitmentStatus()).isEqualTo(IN_PROGRESS),
                () -> assertThat(study.getMinimumAttendanceForGraduation()).isEqualTo(JPA.getMinimumAttendanceForGraduation()),
                () -> assertThat(study.getHashtags()).containsExactlyInAnyOrderElementsOf(JPA.getHashtags())
        );
    }
}
