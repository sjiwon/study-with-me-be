package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
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
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.TXT_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1_PREVIOUS;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.FILE;
import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> SubmitWeeklyAssignmentService 테스트")
class SubmitWeeklyAssignmentServiceTest extends UseCaseTest {
    @InjectMocks
    private SubmitWeeklyAssignmentService submitWeeklyAssignmentService;

    @Mock
    private StudyWeeklyRepository studyWeeklyRepository;

    @Mock
    private StudyParticipantRepository studyParticipantRepository;

    @Mock
    private StudyAttendanceRepository studyAttendanceRepository;

    @Mock
    private FileUploader uploader;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyWeekly previousWeekly = STUDY_WEEKLY_1_PREVIOUS.toWeeklyWithAssignment(study.getId(), host.getId())
            .apply(1L, LocalDateTime.now());
    private final StudyWeekly currentWeekly = STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId())
            .apply(2L, LocalDateTime.now());
    private final StudyAttendance attendance = StudyAttendance.recordAttendance(host.getId(), host.getId(), 1, ABSENCE)
            .apply(1L, LocalDateTime.now());

    private RawFileData fileData;
    private int previousScore;

    @BeforeEach
    void setUp() throws IOException {
        final MultipartFile file = createMultipleMockMultipartFile("hello1.txt", "text/plain");
        fileData = new RawFileData(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
        previousScore = host.getScore().getValue();
    }

    @Test
    @DisplayName("과제 제출물은 링크 또는 파일 중 하나를 반드시 업로드해야 하고 그러지 않으면 과제 제출에 실패한다")
    void throwExceptionByMissingSubmission() {
        assertThatThrownBy(() -> submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
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
                () -> verify(studyWeeklyRepository, times(0)).findById(any()),
                () -> verify(studyParticipantRepository, times(0)).findParticipant(any(), any()),
                () -> verify(uploader, times(0)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(0)).getParticipantAttendanceByWeek(any(), any(), anyInt()),
                () -> verify(studyAttendanceRepository, times(0)).updateParticipantStatus(any(), anyInt(), any(), any())
        );
    }

    @Test
    @DisplayName("과제 제출물은 링크 또는 파일 중 한가지만 업로드해야 하고 그러지 않으면 과제 제출에 실패한다")
    void throwExceptionByDuplicateSubmission() {
        assertThatThrownBy(() -> submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
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
                () -> verify(studyWeeklyRepository, times(0)).findById(any()),
                () -> verify(studyParticipantRepository, times(0)).findParticipant(any(), any()),
                () -> verify(uploader, times(0)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(0)).getParticipantAttendanceByWeek(any(), any(), anyInt()),
                () -> verify(studyAttendanceRepository, times(0)).updateParticipantStatus(any(), anyInt(), any(), any())
        );
    }

    @Test
    @DisplayName("존재하지 않는 주차에 대해서 과제 제출을 하려고 시도하면 실패한다")
    void throwExceptionByWeekNotFound() {
        // given
        given(studyWeeklyRepository.findById(any())).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        currentWeekly.getId(),
                        LINK,
                        null,
                        "https://notion.so"
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(0)).findParticipant(any(), any()),
                () -> verify(uploader, times(0)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(0)).getParticipantAttendanceByWeek(any(), any(), anyInt()),
                () -> verify(studyAttendanceRepository, times(0)).updateParticipantStatus(any(), anyInt(), any(), any())
        );
    }

    @Test
    @DisplayName("해당 주차 과제를 제출한다 [링크 제출] -> 기간안에 제출")
    void successWithLinkA() {
        // given
        given(studyWeeklyRepository.findById(any())).willReturn(Optional.of(currentWeekly));
        given(studyParticipantRepository.findParticipant(any(), any())).willReturn(Optional.of(host));

        // when
        submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
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
                () -> verify(studyWeeklyRepository, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(1)).findParticipant(any(), any()),
                () -> verify(uploader, times(0)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(0)).getParticipantAttendanceByWeek(any(), any(), anyInt()),
                () -> verify(studyAttendanceRepository, times(1)).updateParticipantStatus(any(), anyInt(), any(), any()),
                () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore + 1) // 출석 완료
        );
    }

    @Test
    @DisplayName("해당 주차 과제를 제출한다 [링크 제출] -> 스케줄러에 의한 결석 처리 = 지각으로 수정")
    void successWithLinkB() {
        // given
        given(studyWeeklyRepository.findById(any())).willReturn(Optional.of(previousWeekly));
        given(studyParticipantRepository.findParticipant(any(), any())).willReturn(Optional.of(host));
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(any(), any(), anyInt())).willReturn(Optional.of(attendance));

        // when
        submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
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
                () -> verify(studyWeeklyRepository, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(1)).findParticipant(any(), any()),
                () -> verify(uploader, times(0)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(1)).getParticipantAttendanceByWeek(any(), any(), anyInt()),
                () -> verify(studyAttendanceRepository, times(1)).updateParticipantStatus(any(), anyInt(), any(), any()),
                () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore + 4) // 결석 -> 지각 처리로 수정 [-5 -> -1]
        );
    }

    @Test
    @DisplayName("해당 주차 과제를 제출한다 [파일 제출] -> 기간안에 제출")
    void successWithFileA() {
        // given
        given(studyWeeklyRepository.findById(any())).willReturn(Optional.of(currentWeekly));
        given(studyParticipantRepository.findParticipant(any(), any())).willReturn(Optional.of(host));
        given(uploader.uploadWeeklySubmit(fileData)).willReturn(TXT_FILE.getLink());

        // when
        submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        currentWeekly.getId(),
                        FILE,
                        fileData,
                        null
                )
        );

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(1)).findParticipant(any(), any()),
                () -> verify(uploader, times(1)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(0)).getParticipantAttendanceByWeek(any(), any(), anyInt()),
                () -> verify(studyAttendanceRepository, times(1)).updateParticipantStatus(any(), anyInt(), any(), any()),
                () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore + 1) // 출석 완료
        );
    }

    @Test
    @DisplayName("해당 주차 과제를 제출한다 [파일 제출] -> 스케줄러에 의한 결석 처리 = 지각으로 수정")
    void successWithFileB() {
        // given
        given(studyWeeklyRepository.findById(any())).willReturn(Optional.of(previousWeekly));
        given(studyParticipantRepository.findParticipant(any(), any())).willReturn(Optional.of(host));
        given(uploader.uploadWeeklySubmit(fileData)).willReturn(TXT_FILE.getLink());
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(any(), any(), anyInt())).willReturn(Optional.of(attendance));

        // when
        submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        previousWeekly.getId(),
                        FILE,
                        fileData,
                        null
                )
        );

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(1)).findParticipant(any(), any()),
                () -> verify(uploader, times(1)).uploadWeeklySubmit(any()),
                () -> verify(studyAttendanceRepository, times(1)).getParticipantAttendanceByWeek(any(), any(), anyInt()),
                () -> verify(studyAttendanceRepository, times(1)).updateParticipantStatus(any(), anyInt(), any(), any()),
                () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore + 4) // 결석 -> 지각 처리로 수정 [-5 -> -1]
        );
    }
}
