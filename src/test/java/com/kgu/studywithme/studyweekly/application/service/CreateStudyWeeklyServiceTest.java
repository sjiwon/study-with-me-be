package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import com.kgu.studywithme.upload.utils.FileUploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.StudyWeeklyAttachmentFixture.HWPX_FILE;
import static com.kgu.studywithme.fixture.StudyWeeklyAttachmentFixture.TXT_FILE;
import static com.kgu.studywithme.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> CreateStudyWeeklyService 테스트")
class CreateStudyWeeklyServiceTest extends UseCaseTest {
    @InjectMocks
    private CreateStudyWeeklyService createStudyWeeklyService;

    @Mock
    private FileUploader uploader;

    @Mock
    private StudyWeeklyRepository studyWeeklyRepository;

    @Mock
    private StudyParticipantRepository studyParticipantRepository;

    @Mock
    private StudyAttendanceRepository studyAttendanceRepository;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId());
    private MultipartFile file1;
    private MultipartFile file2;
    private List<MultipartFile> files;

    @BeforeEach
    void setUp() throws IOException {
        file1 = createMultipleMockMultipartFile("hello1.txt", "text/plain");
        file2 = createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml");
        files = List.of(file1, file2);
    }

    @Test
    @DisplayName("스터디 주차를 생성한다")
    void createWeekly() {
        // given
        given(uploader.uploadWeeklyAttachment(file1)).willReturn(TXT_FILE.getLink());
        given(uploader.uploadWeeklyAttachment(file2)).willReturn(HWPX_FILE.getLink());
        given(studyWeeklyRepository.getNextWeek(any())).willReturn(1);
        given(studyWeeklyRepository.save(any())).willReturn(weekly);
        given(studyParticipantRepository.findStudyParticipantIds(any())).willReturn(List.of(1L, 2L, 3L));

        // when
        createStudyWeeklyService.createStudyWeekly(
                new CreateStudyWeeklyUseCase.Command(
                        study.getId(),
                        host.getId(),
                        STUDY_WEEKLY_1.getTitle(),
                        STUDY_WEEKLY_1.getContent(),
                        STUDY_WEEKLY_1.getPeriod().toPeriod(),
                        STUDY_WEEKLY_1.isAssignmentExists(),
                        STUDY_WEEKLY_1.isAutoAttendance(),
                        files
                )
        );

        // then
        verify(uploader, times(files.size())).uploadWeeklyAttachment(any());
        verify(studyWeeklyRepository, times(1)).getNextWeek(any());
        verify(studyWeeklyRepository, times(1)).save(any());
        verify(studyParticipantRepository, times(1)).findStudyParticipantIds(any());
        verify(studyAttendanceRepository, times(1)).saveAll(any());
    }
}
