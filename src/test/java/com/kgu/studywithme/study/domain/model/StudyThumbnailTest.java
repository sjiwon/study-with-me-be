package com.kgu.studywithme.study.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_APTITUDE_NCS_001;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_CERTIFICATION_001;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_ETC_001;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_INTERVIEW_001;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_LANGUAGE_001;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_PROGRAMMING_001;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> 도메인 [StudyThumbnail VO] 테스트")
class StudyThumbnailTest extends ParallelTest {
    @Test
    @DisplayName("특정 스터디 썸네일을 조회한다")
    void findSpecificStudyThumbnail() {
        // given
        final String language = "language_IELTS.png";
        final String interview = "interview_samsung.png";
        final String programming = "programming_C.png";
        final String aptituteAndNcs = "aptitude_ncs_001.png";
        final String certification = "certification_CIP.png";
        final String etc = "etc_teacherExam.png";

        // when - then
        assertAll(
                () -> assertThat(StudyThumbnail.from(language)).isEqualTo(IMAGE_LANGUAGE_001),
                () -> assertThat(StudyThumbnail.from(interview)).isEqualTo(IMAGE_INTERVIEW_001),
                () -> assertThat(StudyThumbnail.from(programming)).isEqualTo(IMAGE_PROGRAMMING_001),
                () -> assertThat(StudyThumbnail.from(aptituteAndNcs)).isEqualTo(IMAGE_APTITUDE_NCS_001),
                () -> assertThat(StudyThumbnail.from(certification)).isEqualTo(IMAGE_CERTIFICATION_001),
                () -> assertThat(StudyThumbnail.from(etc)).isEqualTo(IMAGE_ETC_001)
        );
    }

    @Test
    @DisplayName("제공해주지 않는 썸네일을 조회하면 예외가 발생한다")
    void throwExceptionByStudyThumbnailNotFound() {
        assertThatThrownBy(() -> StudyThumbnail.from("whoareyou.png"))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_THUMBNAIL_NOT_FOUND.getMessage());
    }
}
