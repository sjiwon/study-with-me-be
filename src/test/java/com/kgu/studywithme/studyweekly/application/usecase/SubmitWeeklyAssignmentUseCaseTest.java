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
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentCommand;
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
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> SubmitWeeklyAssignmentUseCase 테스트")
class SubmitWeeklyAssignmentUseCaseTest extends UseCaseTest {
    private final AssignmentUploader assignmentUploader = new AssignmentUploader(new StubFileUploader());
    private final StudyWeeklyRepository studyWeeklyRepository = mock(StudyWeeklyRepository.class);
    private final ParticipateMemberReader participateMemberReader = fakeParticipateMemberReader();
    private final StudyWeeklySubmitRepository studyWeeklySubmitRepository = mock(StudyWeeklySubmitRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final WeeklySubmitManager weeklySubmitManager = new WeeklySubmitManager(
            studyWeeklyRepository,
            participateMemberReader,
            studyWeeklySubmitRepository,
            studyAttendanceRepository
    );
    private final SubmitWeeklyAssignmentUseCase sut = new SubmitWeeklyAssignmentUseCase(assignmentUploader, weeklySubmitManager);

    private final Member host = JIWON.toMember().apply(1L);
    private final Study study = SPRING.toStudy(host.getId()).apply(1L);
    private StudyWeekly weekly;
    private RawFileData file;

    @BeforeEach
    void setUp() throws IOException {
        weekly = StudyWeekly.createWeeklyWithAssignment(
                study.getId(),
                host.getId(),
                STUDY_WEEKLY_1.getTitle(),
                STUDY_WEEKLY_1.getContent(),
                STUDY_WEEKLY_1.getWeek(),
                STUDY_WEEKLY_1.getPeriod().toPeriod(),
                false,
                STUDY_WEEKLY_1.getAttachments()
        ).apply(1L);
        file = FileConverter.convertAssignmentFile(createMultipleMockMultipartFile("hello3.pdf", "application/pdf"));
    }

    @Test
    @DisplayName("해당 주차 과제를 제출한다 [링크 제출]")
    void successWithLink() {
        // given
        final SubmitWeeklyAssignmentCommand command = new SubmitWeeklyAssignmentCommand(
                host.getId(),
                study.getId(),
                weekly.getId(),
                LINK,
                null,
                "htts://notion.so/assignment"
        );
        given(studyWeeklyRepository.getById(command.weeklyId())).willReturn(weekly);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).getById(command.weeklyId()),
                () -> verify(studyAttendanceRepository, times(0))
                        .getParticipantAttendanceByWeek(weekly.getStudyId(), host.getId(), weekly.getWeek()),
                () -> assertThat(weekly.getSubmits()).hasSize(1),
                () -> assertThat(weekly.getSubmits())
                        .map(StudyWeeklySubmit::getUploadAssignment)
                        .map(UploadAssignment::getSubmitType)
                        .containsExactlyInAnyOrder(LINK),
                () -> assertThat(weekly.getSubmits())
                        .map(StudyWeeklySubmit::getUploadAssignment)
                        .map(UploadAssignment::getLink)
                        .containsExactlyInAnyOrder(command.linkSubmit())
        );
    }

    @Test
    @DisplayName("해당 주차 과제를 제출한다 [파일 제출]")
    void successWithFile() {
        // given
        final SubmitWeeklyAssignmentCommand command = new SubmitWeeklyAssignmentCommand(
                host.getId(),
                study.getId(),
                weekly.getId(),
                FILE,
                file,
                null
        );
        given(studyWeeklyRepository.getById(command.weeklyId())).willReturn(weekly);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).getById(command.weeklyId()),
                () -> verify(studyAttendanceRepository, times(0))
                        .getParticipantAttendanceByWeek(weekly.getStudyId(), host.getId(), weekly.getWeek()),
                () -> assertThat(weekly.getSubmits()).hasSize(1),
                () -> assertThat(weekly.getSubmits())
                        .map(StudyWeeklySubmit::getUploadAssignment)
                        .map(UploadAssignment::getSubmitType)
                        .containsExactlyInAnyOrder(FILE),
                () -> assertThat(weekly.getSubmits())
                        .map(StudyWeeklySubmit::getUploadAssignment)
                        .map(UploadAssignment::getUploadFileName)
                        .containsExactlyInAnyOrder(file.fileName()),
                () -> assertThat(weekly.getSubmits())
                        .map(StudyWeeklySubmit::getUploadAssignment)
                        .map(UploadAssignment::getLink)
                        .containsExactlyInAnyOrder("S3/" + file.fileName())
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
