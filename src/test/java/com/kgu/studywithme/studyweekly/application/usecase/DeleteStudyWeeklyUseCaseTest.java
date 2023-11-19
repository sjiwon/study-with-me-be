package com.kgu.studywithme.studyweekly.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.application.usecase.command.DeleteStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyAttachmentRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklySubmitRepository;
import com.kgu.studywithme.studyweekly.domain.service.WeeklyManager;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> DeleteStudyWeeklyUseCase 테스트")
class DeleteStudyWeeklyUseCaseTest extends UseCaseTest {
    private final StudyWeeklyRepository studyWeeklyRepository = mock(StudyWeeklyRepository.class);
    private final StudyWeeklyAttachmentRepository studyWeeklyAttachmentRepository = mock(StudyWeeklyAttachmentRepository.class);
    private final StudyWeeklySubmitRepository studyWeeklySubmitRepository = mock(StudyWeeklySubmitRepository.class);
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final WeeklyManager weeklyManager = new WeeklyManager(
            studyWeeklyRepository,
            studyParticipantRepository,
            studyAttendanceRepository,
            studyWeeklyAttachmentRepository,
            studyWeeklySubmitRepository
    );
    private final DeleteStudyWeeklyUseCase sut = new DeleteStudyWeeklyUseCase(studyWeeklyRepository, weeklyManager);

    private final Member host = JIWON.toMember().apply(1L);
    private final Study study = SPRING.toStudy(host).apply(1L);
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()).apply(1L);
    private final DeleteStudyWeeklyCommand command = new DeleteStudyWeeklyCommand(study.getId(), weekly.getId());

    @Test
    @DisplayName("가장 최신 주차가 아니면 해당 주차 정보를 삭제할 수 없다")
    void throwExceptionBySpecificWeekIsNotLatestWeek() {
        // given
        given(studyWeeklyRepository.isLatestWeek(command.studyId(), command.weeklyId())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.ONLY_LATEST_WEEKLY_CAN_DELETE.getMessage());

        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).isLatestWeek(command.studyId(), command.weeklyId()),
                () -> verify(studyWeeklyRepository, times(0)).getById(command.weeklyId()),
                () -> verify(studyAttendanceRepository, times(0)).deleteFromSpecificWeekly(weekly.getStudyId(), weekly.getWeek()),
                () -> verify(studyWeeklySubmitRepository, times(0)).deleteFromSpecificWeekly(weekly.getId()),
                () -> verify(studyWeeklyAttachmentRepository, times(0)).deleteFromSpecificWeekly(weekly.getId()),
                () -> verify(studyWeeklyRepository, times(0)).deleteById(weekly.getId())
        );
    }

    @Test
    @DisplayName("해당 주차 정보를 삭제한다")
    void success() {
        // given
        given(studyWeeklyRepository.isLatestWeek(command.studyId(), command.weeklyId())).willReturn(true);
        given(studyWeeklyRepository.getById(command.weeklyId())).willReturn(weekly);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).isLatestWeek(command.studyId(), command.weeklyId()),
                () -> verify(studyWeeklyRepository, times(1)).getById(command.weeklyId()),
                () -> verify(studyAttendanceRepository, times(1)).deleteFromSpecificWeekly(weekly.getStudyId(), weekly.getWeek()),
                () -> verify(studyWeeklySubmitRepository, times(1)).deleteFromSpecificWeekly(weekly.getId()),
                () -> verify(studyWeeklyAttachmentRepository, times(1)).deleteFromSpecificWeekly(weekly.getId()),
                () -> verify(studyWeeklyRepository, times(1)).deleteById(weekly.getId())
        );
    }
}
