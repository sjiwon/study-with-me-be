package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.attachment.StudyWeeklyAttachment;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import com.kgu.studywithme.studyweekly.infrastructure.persistence.StudyWeeklyJpaRepository;
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

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.HWPX_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.IMG_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.PDF_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.TXT_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> UpdateStudyWeeklyService 테스트")
class UpdateStudyWeeklyServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdateStudyWeeklyService updateStudyWeeklyService;

    @Mock
    private StudyWeeklyJpaRepository studyWeeklyJpaRepository;

    @Mock
    private FileUploader uploader;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()).apply(1L, LocalDateTime.now());

    private RawFileData fileData1;
    private RawFileData fileData2;
    private RawFileData fileData3;
    private RawFileData fileData4;
    private List<RawFileData> fileDatas;
    private UpdateStudyWeeklyUseCase.Command command;

    @BeforeEach
    void setUp() throws IOException {
        final MultipartFile file1 = createMultipleMockMultipartFile("hello1.txt", "text/plain");
        final MultipartFile file2 = createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml");
        final MultipartFile file3 = createMultipleMockMultipartFile("hello3.pdf", "application/pdf");
        final MultipartFile file4 = createMultipleMockMultipartFile("hello4.png", "image/png");


        fileData1 = new RawFileData(file1.getInputStream(), file1.getContentType(), file1.getOriginalFilename());
        fileData2 = new RawFileData(file2.getInputStream(), file2.getContentType(), file2.getOriginalFilename());
        fileData3 = new RawFileData(file3.getInputStream(), file3.getContentType(), file3.getOriginalFilename());
        fileData4 = new RawFileData(file4.getInputStream(), file4.getContentType(), file4.getOriginalFilename());
        fileDatas = List.of(fileData1, fileData2, fileData3, fileData4);

        command = new UpdateStudyWeeklyUseCase.Command(
                weekly.getId(),
                STUDY_WEEKLY_2.getTitle(),
                STUDY_WEEKLY_2.getContent(),
                STUDY_WEEKLY_2.getPeriod().toPeriod(),
                STUDY_WEEKLY_2.isAssignmentExists(),
                STUDY_WEEKLY_2.isAutoAttendance(),
                fileDatas
        );
    }

    @Test
    @DisplayName("해당 주차 정보를 찾지 못하면 수정할 수 없다")
    void throwExceptionByWeeklyNotFound() {
        // given
        given(studyWeeklyJpaRepository.findById(any())).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> updateStudyWeeklyService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyWeeklyJpaRepository, times(1)).findById(any()),
                () -> verify(uploader, times(0)).uploadWeeklyAttachment(any())
        );
    }

    @Test
    @DisplayName("해당 주차 정보를 수정한다")
    void success() {
        // given
        given(studyWeeklyJpaRepository.findById(any())).willReturn(Optional.of(weekly));
        given(uploader.uploadWeeklyAttachment(fileData1)).willReturn(TXT_FILE.getLink());
        given(uploader.uploadWeeklyAttachment(fileData2)).willReturn(HWPX_FILE.getLink());
        given(uploader.uploadWeeklyAttachment(fileData3)).willReturn(PDF_FILE.getLink());
        given(uploader.uploadWeeklyAttachment(fileData4)).willReturn(IMG_FILE.getLink());

        // when
        updateStudyWeeklyService.invoke(command);

        // then
        assertAll(
                () -> verify(studyWeeklyJpaRepository, times(1)).findById(any()),
                () -> verify(uploader, times(fileDatas.size())).uploadWeeklyAttachment(any()),
                () -> assertThat(weekly.getTitle()).isEqualTo(STUDY_WEEKLY_2.getTitle()),
                () -> assertThat(weekly.getContent()).isEqualTo(STUDY_WEEKLY_2.getContent()),
                () -> assertThat(weekly.getPeriod().getStartDate()).isEqualTo(STUDY_WEEKLY_2.getPeriod().getStartDate()),
                () -> assertThat(weekly.getPeriod().getEndDate()).isEqualTo(STUDY_WEEKLY_2.getPeriod().getEndDate()),
                () -> assertThat(weekly.isAssignmentExists()).isEqualTo(STUDY_WEEKLY_2.isAssignmentExists()),
                () -> assertThat(weekly.isAutoAttendance()).isEqualTo(STUDY_WEEKLY_2.isAutoAttendance()),
                () -> assertThat(weekly.getAttachments())
                        .map(StudyWeeklyAttachment::getUploadAttachment)
                        .map(UploadAttachment::getLink)
                        .containsExactlyInAnyOrder(
                                TXT_FILE.getLink(),
                                HWPX_FILE.getLink(),
                                PDF_FILE.getLink(),
                                IMG_FILE.getLink()
                        )
        );
    }
}
