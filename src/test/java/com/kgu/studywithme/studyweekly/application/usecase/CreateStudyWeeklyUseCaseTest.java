package com.kgu.studywithme.studyweekly.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.common.mock.stub.StubFileUploader;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyAttachmentRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklySubmitRepository;
import com.kgu.studywithme.studyweekly.domain.service.AttachmentUploader;
import com.kgu.studywithme.studyweekly.domain.service.WeeklyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> CreateStudyWeeklyUseCase 테스트")
class CreateStudyWeeklyUseCaseTest extends UseCaseTest {
    private final AttachmentUploader attachmentUploader = new AttachmentUploader(new StubFileUploader());
    private final StudyWeeklyRepository studyWeeklyRepository = mock(StudyWeeklyRepository.class);
    private final StudyWeeklyAttachmentRepository studyWeeklyAttachmentRepository = mock(StudyWeeklyAttachmentRepository.class);
    private final StudyWeeklySubmitRepository studyWeeklySubmitRepository = mock(StudyWeeklySubmitRepository.class);
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final WeeklyManager weeklyManager
            = new WeeklyManager(studyWeeklyRepository, studyParticipantRepository, studyAttendanceRepository, studyWeeklyAttachmentRepository, studyWeeklySubmitRepository);
    private final CreateStudyWeeklyUseCase sut
            = new CreateStudyWeeklyUseCase(attachmentUploader, studyWeeklyRepository, weeklyManager);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member participantA = GHOST.toMember().apply(2L);
    private final Member participantB = ANONYMOUS.toMember().apply(3L);
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L);
    private CreateStudyWeeklyCommand command;

    @BeforeEach
    void setUp() throws IOException {
        final List<RawFileData> files = FileConverter.convertAttachmentFiles(List.of(
                createMultipleMockMultipartFile("hello1.txt", "text/plain"),
                createMultipleMockMultipartFile("hello3.pdf", "application/pdf")
        ));
        command = new CreateStudyWeeklyCommand(
                study.getId(),
                host.getId(),
                STUDY_WEEKLY_1.getTitle(),
                STUDY_WEEKLY_1.getContent(),
                STUDY_WEEKLY_1.getPeriod().toPeriod(),
                STUDY_WEEKLY_1.isAssignmentExists(),
                STUDY_WEEKLY_1.isAutoAttendance(),
                files
        );
    }

    @Test
    @DisplayName("스터디 주차를 생성한다")
    void createStudyWeekly() {
        // given
        given(studyWeeklyRepository.getNextWeek(command.studyId())).willReturn(1);

        final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()).apply(1L);
        given(studyWeeklyRepository.save(any())).willReturn(weekly);
        given(studyParticipantRepository.findParticipantIdsByStatus(weekly.getStudyId(), APPROVE))
                .willReturn(List.of(host.getId(), participantA.getId(), participantB.getId()));

        // when
        final Long createdWeeklyId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).getNextWeek(command.studyId()),
                () -> verify(studyWeeklyRepository, times(1)).save(any()),
                () -> verify(studyParticipantRepository, times(1)).findParticipantIdsByStatus(weekly.getStudyId(), APPROVE),
                () -> verify(studyAttendanceRepository, times(1)).saveAll(any()),
                () -> assertThat(createdWeeklyId).isEqualTo(weekly.getId())
        );
    }
}
