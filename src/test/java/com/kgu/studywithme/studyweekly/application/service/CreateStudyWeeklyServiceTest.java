package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyweekly.application.adapter.StudyWeeklyHandlingRepositoryAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.event.WeeklyCreatedEvent;
import com.kgu.studywithme.studyweekly.infrastructure.persistence.StudyWeeklyJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.HWPX_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.TXT_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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
    private StudyWeeklyHandlingRepositoryAdapter studyWeeklyHandlingRepositoryAdapter;

    @Mock
    private StudyWeeklyJpaRepository studyWeeklyJpaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private RawFileData fileData1;
    private RawFileData fileData2;
    private List<RawFileData> fileDatas;

    @BeforeEach
    void setUp() throws IOException {
        final MultipartFile file1 = createMultipleMockMultipartFile("hello1.txt", "text/plain");
        final MultipartFile file2 = createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml");

        fileData1 = new RawFileData(file1.getInputStream(), file1.getContentType(), file1.getOriginalFilename());
        fileData2 = new RawFileData(file2.getInputStream(), file2.getContentType(), file2.getOriginalFilename());
        fileDatas = List.of(fileData1, fileData2);
    }

    @Test
    @DisplayName("스터디 주차를 생성한다")
    void createWeekly() {
        // given
        given(uploader.uploadWeeklyAttachment(fileData1)).willReturn(TXT_FILE.getLink());
        given(uploader.uploadWeeklyAttachment(fileData2)).willReturn(HWPX_FILE.getLink());
        given(studyWeeklyHandlingRepositoryAdapter.getNextWeek(any())).willReturn(1);

        final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()).apply(1L, LocalDateTime.now());
        given(studyWeeklyJpaRepository.save(any())).willReturn(weekly);

        // when
        final Long createdWeeklyId = createStudyWeeklyService.invoke(
                new CreateStudyWeeklyUseCase.Command(
                        study.getId(),
                        host.getId(),
                        STUDY_WEEKLY_1.getTitle(),
                        STUDY_WEEKLY_1.getContent(),
                        STUDY_WEEKLY_1.getPeriod().toPeriod(),
                        STUDY_WEEKLY_1.isAssignmentExists(),
                        STUDY_WEEKLY_1.isAutoAttendance(),
                        fileDatas
                )
        );

        // then
        assertAll(
                () -> verify(uploader, times(fileDatas.size())).uploadWeeklyAttachment(any()),
                () -> verify(studyWeeklyHandlingRepositoryAdapter, times(1)).getNextWeek(any()),
                () -> verify(eventPublisher, times(1)).publishEvent(any(WeeklyCreatedEvent.class)),
                () -> assertThat(createdWeeklyId).isEqualTo(weekly.getId())
        );
    }
}
