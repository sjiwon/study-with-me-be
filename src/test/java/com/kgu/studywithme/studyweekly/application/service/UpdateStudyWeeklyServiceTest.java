package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.attachment.StudyWeeklyAttachment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import com.kgu.studywithme.studyweekly.infrastructure.persistence.StudyWeeklyJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> UpdateStudyWeeklyService 테스트")
class UpdateStudyWeeklyServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdateStudyWeeklyService updateStudyWeeklyService;

    @Mock
    private StudyWeeklyJpaRepository studyWeeklyJpaRepository;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()).apply(1L, LocalDateTime.now());

    private final UpdateStudyWeeklyUseCase.Command command = new UpdateStudyWeeklyUseCase.Command(
            weekly.getId(),
            STUDY_WEEKLY_2.getTitle(),
            STUDY_WEEKLY_2.getContent(),
            STUDY_WEEKLY_2.getPeriod().toPeriod(),
            STUDY_WEEKLY_2.isAssignmentExists(),
            STUDY_WEEKLY_2.isAutoAttendance(),
            STUDY_WEEKLY_2.getAttachments()
    );

    @Test
    @DisplayName("해당 주차 정보를 찾지 못하면 수정할 수 없다")
    void throwExceptionByWeeklyNotFound() {
        // given
        given(studyWeeklyJpaRepository.findById(any())).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> updateStudyWeeklyService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND.getMessage());

        verify(studyWeeklyJpaRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("해당 주차 정보를 수정한다")
    void success() {
        // given
        given(studyWeeklyJpaRepository.findById(any())).willReturn(Optional.of(weekly));

        // when
        updateStudyWeeklyService.invoke(command);

        // then
        assertAll(
                () -> verify(studyWeeklyJpaRepository, times(1)).findById(any()),
                () -> assertThat(weekly.getTitle()).isEqualTo(STUDY_WEEKLY_2.getTitle()),
                () -> assertThat(weekly.getContent()).isEqualTo(STUDY_WEEKLY_2.getContent()),
                () -> assertThat(weekly.getPeriod().getStartDate()).isEqualTo(STUDY_WEEKLY_2.getPeriod().getStartDate()),
                () -> assertThat(weekly.getPeriod().getEndDate()).isEqualTo(STUDY_WEEKLY_2.getPeriod().getEndDate()),
                () -> assertThat(weekly.isAssignmentExists()).isEqualTo(STUDY_WEEKLY_2.isAssignmentExists()),
                () -> assertThat(weekly.isAutoAttendance()).isEqualTo(STUDY_WEEKLY_2.isAutoAttendance()),
                () -> assertThat(weekly.getAttachments())
                        .map(StudyWeeklyAttachment::getUploadAttachment)
                        .containsExactlyInAnyOrderElementsOf(STUDY_WEEKLY_2.getAttachments())
        );
    }
}
