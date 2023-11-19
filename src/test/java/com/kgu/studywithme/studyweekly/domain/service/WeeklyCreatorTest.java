package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyCommand;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
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

@DisplayName("StudyWeekly -> WeeklyCreator 테스트")
class WeeklyCreatorTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final StudyWeeklyRepository studyWeeklyRepository = mock(StudyWeeklyRepository.class);
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final WeeklyCreator sut = new WeeklyCreator(
            studyRepository,
            memberRepository,
            studyWeeklyRepository,
            studyParticipantRepository,
            studyAttendanceRepository
    );

    private final Member host = JIWON.toMember().apply(1L);
    private final Member participantA = GHOST.toMember().apply(2L);
    private final Member participantB = ANONYMOUS.toMember().apply(3L);
    private final Study study = SPRING.toStudy(host).apply(1L);
    private CreateStudyWeeklyCommand command;
    private List<UploadAttachment> attachments;

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
        attachments = List.of(
                new UploadAttachment("hello1.txt", "S3/hello1.txt"),
                new UploadAttachment("hello3.pdf", "S3/hello3.pdf")
        );
    }

    @Test
    @DisplayName("특정 주차를 생성한다")
    void success() {
        // given
        given(studyRepository.getInProgressStudy(command.studyId())).willReturn(study);
        given(memberRepository.getById(command.creatorId())).willReturn(host);

        final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study, host).apply(1L);
        given(studyWeeklyRepository.save(any(StudyWeekly.class))).willReturn(weekly);
        given(studyParticipantRepository.findParticipantsByStatus(study.getId(), APPROVE)).willReturn(List.of(host, participantA, participantB));

        // when
        final StudyWeekly savedWeekly = sut.invoke(command, attachments, STUDY_WEEKLY_1.getWeek());

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getInProgressStudy(command.studyId()),
                () -> verify(memberRepository, times(1)).getById(command.creatorId()),
                () -> verify(studyWeeklyRepository, times(1)).save(any(StudyWeekly.class)),
                () -> verify(studyParticipantRepository, times(1)).findParticipantsByStatus(study.getId(), APPROVE),
                () -> verify(studyAttendanceRepository, times(1)).saveAll(any()),
                () -> assertThat(savedWeekly.getId()).isEqualTo(weekly.getId()),
                () -> assertThat(savedWeekly.getStudy()).isEqualTo(study),
                () -> assertThat(savedWeekly.getCreator()).isEqualTo(host),
                () -> assertThat(savedWeekly.getWeek()).isEqualTo(STUDY_WEEKLY_1.getWeek()),
                () -> assertThat(savedWeekly.getAttachments()).hasSize(attachments.size())
        );
    }
}
