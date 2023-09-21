package com.kgu.studywithme.studyweekly.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.common.mock.stub.StubFileUploader;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklyAttachment;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
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

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> UpdateStudyWeeklyUseCase 테스트")
class UpdateStudyWeeklyUseCaseTest extends UseCaseTest {
    private final AttachmentUploader attachmentUploader = new AttachmentUploader(new StubFileUploader());
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
    private final UpdateStudyWeeklyUseCase sut = new UpdateStudyWeeklyUseCase(attachmentUploader, weeklyManager);

    private final Member host = JIWON.toMember().apply(1L);
    private final Study study = SPRING.toStudy(host.getId()).apply(1L);
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()).apply(1L);
    private UpdateStudyWeeklyCommand command;

    @BeforeEach
    void setUp() throws IOException {
        final List<RawFileData> files = FileConverter.convertAttachmentFiles(List.of(
                createMultipleMockMultipartFile("hello1.txt", "text/plain"),
                createMultipleMockMultipartFile("hello3.pdf", "application/pdf")

        ));
        command = new UpdateStudyWeeklyCommand(
                weekly.getId(),
                STUDY_WEEKLY_2.getTitle(),
                STUDY_WEEKLY_2.getContent(),
                STUDY_WEEKLY_2.getPeriod().toPeriod(),
                STUDY_WEEKLY_2.isAssignmentExists(),
                STUDY_WEEKLY_2.isAutoAttendance(),
                files
        );
    }

    @Test
    @DisplayName("해당 주차 정보를 수정한다")
    void updateStudyWeekly() {
        // given
        given(studyWeeklyRepository.getById(command.weeklyId())).willReturn(weekly);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).getById(command.weeklyId()),
                () -> assertThat(weekly.getTitle()).isEqualTo(command.title()),
                () -> assertThat(weekly.getContent()).isEqualTo(command.content()),
                () -> assertThat(weekly.getPeriod().getStartDate()).isEqualTo(command.period().getStartDate()),
                () -> assertThat(weekly.getPeriod().getEndDate()).isEqualTo(command.period().getEndDate()),
                () -> assertThat(weekly.isAssignmentExists()).isEqualTo(command.assignmentExists()),
                () -> assertThat(weekly.isAutoAttendance()).isEqualTo(command.autoAttendance()),
                () -> assertThat(weekly.getAttachments())
                        .map(StudyWeeklyAttachment::getUploadAttachment)
                        .map(UploadAttachment::getUploadFileName)
                        .containsExactlyInAnyOrder("hello1.txt", "hello3.pdf"),
                () -> assertThat(weekly.getAttachments())
                        .map(StudyWeeklyAttachment::getUploadAttachment)
                        .map(UploadAttachment::getLink)
                        .containsExactlyInAnyOrder("S3/hello1.txt", "S3/hello3.pdf")
        );
    }
}
