package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyweekly.application.adapter.StudyWeeklyHandlingRepositoryAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.DeleteStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> DeleteStudyWeeklyService 테스트")
class DeleteStudyWeeklyServiceTest extends UseCaseTest {
    @InjectMocks
    private DeleteStudyWeeklyService deleteStudyWeeklyService;

    @Mock
    private StudyWeeklyHandlingRepositoryAdapter studyWeeklyHandlingRepositoryAdapter;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()).apply(1L, LocalDateTime.now());
    private final DeleteStudyWeeklyUseCase.Command command = new DeleteStudyWeeklyUseCase.Command(
            study.getId(),
            weekly.getId()
    );

    @Test
    @DisplayName("가장 최신 주차가 아니면 해당 주차 정보를 삭제할 수 없다")
    void throwExceptionBySpecificWeekIsNotLatestWeek() {
        // given
        given(studyWeeklyHandlingRepositoryAdapter.isLatestWeek(any(), any())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> deleteStudyWeeklyService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.ONLY_LATEST_WEEKLY_CAN_DELETE.getMessage());

        assertAll(
                () -> verify(studyWeeklyHandlingRepositoryAdapter, times(1)).isLatestWeek(any(), any()),
                () -> verify(studyWeeklyHandlingRepositoryAdapter, times(0)).deleteSpecificWeekly(any(), any())
        );
    }

    @Test
    @DisplayName("해당 주차 정보를 삭제한다")
    void success() {
        // given
        given(studyWeeklyHandlingRepositoryAdapter.isLatestWeek(any(), any())).willReturn(true);

        // when
        deleteStudyWeeklyService.invoke(command);

        // then
        assertAll(
                () -> verify(studyWeeklyHandlingRepositoryAdapter, times(1)).isLatestWeek(any(), any()),
                () -> verify(studyWeeklyHandlingRepositoryAdapter, times(1)).deleteSpecificWeekly(any(), any())
        );
    }
}
