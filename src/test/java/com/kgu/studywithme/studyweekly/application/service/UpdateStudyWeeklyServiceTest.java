package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.attachment.StudyWeeklyAttachment;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
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
import java.util.Optional;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.StudyWeeklyAttachmentFixture.*;
import static com.kgu.studywithme.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> UpdateStudyWeeklyService 테스트")
class UpdateStudyWeeklyServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdateStudyWeeklyService updateStudyWeeklyService;

    @Mock
    private StudyWeeklyRepository studyWeeklyRepository;

    @Mock
    private FileUploader uploader;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId());
    private MultipartFile file1;
    private MultipartFile file2;
    private MultipartFile file3;
    private MultipartFile file4;
    private List<MultipartFile> files;

    @BeforeEach
    void setUp() throws IOException {
        file1 = createMultipleMockMultipartFile("hello1.txt", "text/plain");
        file2 = createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml");
        file3 = createMultipleMockMultipartFile("hello3.pdf", "application/pdf");
        file4 = createMultipleMockMultipartFile("hello4.png", "image/png");
        files = List.of(file1, file2, file3, file4);
    }

    @Test
    @DisplayName("해당 주차 정보를 찾지 못하면 수정할 수 없다")
    void throwExceptionByWeeklyNotFound() {
        // given
        given(studyWeeklyRepository.getSpecificWeekly(any(), anyInt())).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> updateStudyWeeklyService.updateStudyWeekly(
                new UpdateStudyWeeklyUseCase.Command(
                        study.getId(),
                        STUDY_WEEKLY_1.getWeek(),
                        STUDY_WEEKLY_2.getTitle(),
                        STUDY_WEEKLY_2.getContent(),
                        STUDY_WEEKLY_2.getPeriod().toPeriod(),
                        STUDY_WEEKLY_2.isAssignmentExists(),
                        STUDY_WEEKLY_2.isAutoAttendance(),
                        files
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND.getMessage());

        verify(studyWeeklyRepository, times(1)).getSpecificWeekly(any(), anyInt());
        verify(uploader, times(0)).uploadWeeklyAttachment(any());
    }

    @Test
    @DisplayName("해당 주차 정보를 수정한다")
    void success() {
        // given
        given(studyWeeklyRepository.getSpecificWeekly(any(), anyInt())).willReturn(Optional.of(weekly));
        given(uploader.uploadWeeklyAttachment(file1)).willReturn(TXT_FILE.getLink());
        given(uploader.uploadWeeklyAttachment(file2)).willReturn(HWPX_FILE.getLink());
        given(uploader.uploadWeeklyAttachment(file3)).willReturn(PDF_FILE.getLink());
        given(uploader.uploadWeeklyAttachment(file4)).willReturn(IMG_FILE.getLink());

        // when
        updateStudyWeeklyService.updateStudyWeekly(
                new UpdateStudyWeeklyUseCase.Command(
                        study.getId(),
                        STUDY_WEEKLY_1.getWeek(),
                        STUDY_WEEKLY_2.getTitle(),
                        STUDY_WEEKLY_2.getContent(),
                        STUDY_WEEKLY_2.getPeriod().toPeriod(),
                        STUDY_WEEKLY_2.isAssignmentExists(),
                        STUDY_WEEKLY_2.isAutoAttendance(),
                        files
                )
        );

        // then
        verify(studyWeeklyRepository, times(1)).getSpecificWeekly(any(), anyInt());
        verify(uploader, times(files.size())).uploadWeeklyAttachment(any());

        assertAll(
                () -> assertThat(weekly.getTitle()).isEqualTo(STUDY_WEEKLY_2.getTitle()),
                () -> assertThat(weekly.getContent()).isEqualTo(STUDY_WEEKLY_2.getContent()),
                () -> assertThat(weekly.getPeriod().getStartDate()).isEqualTo(STUDY_WEEKLY_2.getPeriod().getStartDate()),
                () -> assertThat(weekly.getPeriod().getEndDate()).isEqualTo(STUDY_WEEKLY_2.getPeriod().getEndDate()),
                () -> assertThat(weekly.isAssignmentExists()).isEqualTo(STUDY_WEEKLY_2.isAssignmentExists()),
                () -> assertThat(weekly.isAutoAttendance()).isEqualTo(STUDY_WEEKLY_2.isAutoAttendance()),
                () -> assertThat(
                        weekly.getAttachments()
                                .stream()
                                .map(StudyWeeklyAttachment::getUploadAttachment)
                                .map(UploadAttachment::getLink)
                                .toList()
                ).containsExactlyInAnyOrder(
                        TXT_FILE.getLink(),
                        HWPX_FILE.getLink(),
                        PDF_FILE.getLink(),
                        IMG_FILE.getLink()
                )
        );
    }
}
