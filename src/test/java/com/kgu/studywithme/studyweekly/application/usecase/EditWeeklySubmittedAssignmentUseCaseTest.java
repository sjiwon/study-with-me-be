package com.kgu.studywithme.studyweekly.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.common.mock.stub.StubFileUploader;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditWeeklySubmittedAssignmentCommand;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklySubmitRepository;
import com.kgu.studywithme.studyweekly.domain.service.AssignmentUploader;
import com.kgu.studywithme.studyweekly.domain.service.WeeklySubmitManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.FILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> EditWeeklySubmittedAssignmentUseCase 테스트")
class EditWeeklySubmittedAssignmentUseCaseTest extends UseCaseTest {
    private final AssignmentUploader assignmentUploader = new AssignmentUploader(new StubFileUploader());
    private final StudyWeeklyRepository studyWeeklyRepository = mock(StudyWeeklyRepository.class);
    private final ParticipateMemberReader participateMemberReader = fakeParticipateMemberReader();
    private final StudyWeeklySubmitRepository studyWeeklySubmitRepository = mock(StudyWeeklySubmitRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final WeeklySubmitManager weeklySubmitManager
            = new WeeklySubmitManager(studyWeeklyRepository, participateMemberReader, studyWeeklySubmitRepository, studyAttendanceRepository);
    private final EditWeeklySubmittedAssignmentUseCase sut
            = new EditWeeklySubmittedAssignmentUseCase(assignmentUploader, weeklySubmitManager);

    private final Member host = JIWON.toMember().apply(1L);
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L);
    private final StudyWeekly weekly = StudyWeekly.createWeeklyWithAssignment(
            study.getId(),
            host.getId(),
            STUDY_WEEKLY_1.getTitle(),
            STUDY_WEEKLY_1.getContent(),
            STUDY_WEEKLY_1.getWeek(),
            STUDY_WEEKLY_1.getPeriod().toPeriod(),
            false,
            STUDY_WEEKLY_1.getAttachments()
    ).apply(1L);
    private final StudyWeeklySubmit submittedAssignment
            = StudyWeeklySubmit.submitAssignment(weekly, host.getId(), UploadAssignment.withLink("https://notion.so/assignment")).apply(1L);
    private RawFileData file;

    @BeforeEach
    void setUp() throws IOException {
        file = FileConverter.convertAssignmentFile(
                createMultipleMockMultipartFile("hello3.pdf", "application/pdf")
        );
    }

    @Test
    @DisplayName("제출한 과제를 수정한다")
    void success() {
        // given
        final EditWeeklySubmittedAssignmentCommand command = new EditWeeklySubmittedAssignmentCommand(
                host.getId(),
                study.getId(),
                weekly.getId(),
                FILE,
                file,
                null
        );
        given(studyWeeklySubmitRepository.getSubmittedAssignment(command.weeklyId(), command.memberId())).willReturn(submittedAssignment);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyWeeklySubmitRepository, times(1)).getSubmittedAssignment(command.weeklyId(), command.memberId()),
                () -> verify(studyAttendanceRepository, times(0))
                        .getParticipantAttendanceByWeek(weekly.getStudyId(), host.getId(), weekly.getWeek()),
                () -> assertThat(submittedAssignment.getUploadAssignment().getSubmitType()).isEqualTo(FILE),
                () -> assertThat(submittedAssignment.getUploadAssignment().getUploadFileName()).isEqualTo(file.fileName()),
                () -> assertThat(submittedAssignment.getUploadAssignment().getLink()).isEqualTo("S3/" + file.fileName())
        );
    }

    private ParticipateMemberReader fakeParticipateMemberReader() {
        return new ParticipateMemberReader() {
            @Override
            public Member getApplier(final Long studyId, final Long memberId) {
                throw StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND);
            }

            @Override
            public Member getParticipant(final Long studyId, final Long memberId) {
                if (memberId.equals(host.getId())) {
                    return host;
                }

                throw StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND);
            }
        };
    }
}
