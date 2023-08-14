package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.file.application.service.FileUploader;
import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.service.QueryMemberByIdService;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditSubmittedWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1_PREVIOUS;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> EditSubmittedWeeklyAssignmentService 테스트")
class EditSubmittedWeeklyAssignmentServiceTest extends UseCaseTest {
    @InjectMocks
    private EditSubmittedWeeklyAssignmentService editSubmittedWeeklyAssignmentService;

    @Mock
    private StudyWeeklyRepository studyWeeklyRepository;

    @Mock
    private StudyAttendanceRepository studyAttendanceRepository;

    @Mock
    private QueryMemberByIdService queryMemberByIdService;

    @Mock
    private FileUploader uploader;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyWeekly previousWeekly =
            STUDY_WEEKLY_1_PREVIOUS.toWeeklyWithAssignment(study.getId(), host.getId())
                    .apply(1L, LocalDateTime.now());
    private final StudyWeekly currentWeekly =
            STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId())
                    .apply(2L, LocalDateTime.now());
    private final StudyAttendance attendance =
            StudyAttendance.recordAttendance(host.getId(), host.getId(), 1, ATTENDANCE)
                    .apply(1L, LocalDateTime.now());

    private RawFileData fileData;
    private int previousScore;

    @BeforeEach
    void setUp() throws IOException {
        final MultipartFile file = createMultipleMockMultipartFile("hello1.txt", "text/plain");
        fileData = new RawFileData(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
        previousScore = host.getScore();
    }

    @Test
    @DisplayName("과제 제출물은 링크 또는 파일 중 하나를 반드시 업로드해야 하고 그러지 않으면 제출한 과제 수정에 실패한다")
    void throwExceptionByMissingSubmission() {
        assertThatThrownBy(() -> editSubmittedWeeklyAssignmentService.invoke(
                new EditSubmittedWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        currentWeekly.getId(),
                        LINK,
                        null,
                        null
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.MISSING_SUBMISSION.getMessage());

        assertAll(
                () -> verify(studyWeeklyRepository, times(0)).getSubmittedAssignment(any(), any(), any()),
                () -> verify(uploader, times(0)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(0)).getParticipantAttendanceByWeek(any(), any(), anyInt())
        );
    }

    @Test
    @DisplayName("과제 제출물은 링크 또는 파일 중 한가지만 업로드해야 하고 그러지 않으면 제출한 과제 수정에 실패한다")
    void throwExceptionByDuplicateSubmission() {
        assertThatThrownBy(() -> editSubmittedWeeklyAssignmentService.invoke(
                new EditSubmittedWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        currentWeekly.getId(),
                        LINK,
                        fileData,
                        "https://notion.so"
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION.getMessage());

        assertAll(
                () -> verify(studyWeeklyRepository, times(0)).getSubmittedAssignment(any(), any(), any()),
                () -> verify(uploader, times(0)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(0)).getParticipantAttendanceByWeek(any(), any(), anyInt())
        );
    }

    @Test
    @DisplayName("제출한 과제가 없다면 수정할 수 없다")
    void throwExceptionBySubmittedAssignmentNotFound() {
        // given
        given(studyWeeklyRepository.getSubmittedAssignment(any(), any(), any())).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> editSubmittedWeeklyAssignmentService.invoke(
                new EditSubmittedWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        currentWeekly.getId(),
                        LINK,
                        null,
                        "https://notion.so"
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.SUBMITTED_ASSIGNMENT_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).getSubmittedAssignment(any(), any(), any()),
                () -> verify(uploader, times(0)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(0)).getParticipantAttendanceByWeek(any(), any(), anyInt())
        );
    }

    @Test
    @DisplayName("제출한 과제를 수정한다 -> 제출 기간 내에 수정")
    void successA() {
        // given
        final StudyWeeklySubmit submittedAssignment = StudyWeeklySubmit.submitAssignment(
                currentWeekly,
                host.getId(),
                UploadAssignment.withLink("https://notion.so")
        ).apply(1L, LocalDateTime.now());
        given(studyWeeklyRepository.getSubmittedAssignment(any(), any(), any())).willReturn(Optional.of(submittedAssignment));
        given(queryMemberByIdService.findById(any())).willReturn(host);

        // when
        editSubmittedWeeklyAssignmentService.invoke(
                new EditSubmittedWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        currentWeekly.getId(),
                        LINK,
                        null,
                        "https://notion.so"
                )
        );

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).getSubmittedAssignment(any(), any(), any()),
                () -> verify(uploader, times(0)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(0)).getParticipantAttendanceByWeek(any(), any(), anyInt()),
                () -> assertThat(host.getScore()).isEqualTo(previousScore) // Score 유지
        );
    }

    @Test
    @DisplayName("제출한 과제를 수정한다 -> 제출 기간 이후에 수정 = 지각으로 출석 정보 수정")
    void successB() {
        // given
        final StudyWeeklySubmit submittedAssignment = StudyWeeklySubmit.submitAssignment(
                previousWeekly,
                host.getId(),
                UploadAssignment.withLink("https://notion.so")
        ).apply(1L, LocalDateTime.now());
        given(studyWeeklyRepository.getSubmittedAssignment(any(), any(), any())).willReturn(Optional.of(submittedAssignment));
        given(queryMemberByIdService.findById(any())).willReturn(host);
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(any(), any(), anyInt())).willReturn(Optional.of(attendance));

        // when
        editSubmittedWeeklyAssignmentService.invoke(
                new EditSubmittedWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        previousWeekly.getId(),
                        LINK,
                        null,
                        "https://notion.so"
                )
        );

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).getSubmittedAssignment(any(), any(), any()),
                () -> verify(uploader, times(0)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(1)).getParticipantAttendanceByWeek(any(), any(), anyInt()),
                () -> assertThat(host.getScore()).isEqualTo(previousScore - 2) // 출석 -> 지각
        );
    }
}
