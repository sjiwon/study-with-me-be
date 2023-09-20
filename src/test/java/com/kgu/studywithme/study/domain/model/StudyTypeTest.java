package com.kgu.studywithme.study.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.study.domain.model.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.model.StudyType.ONLINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> 도메인 [StudyType VO] 테스트")
class StudyTypeTest extends ParallelTest {
    @Test
    @DisplayName("StudyType을 조회한다")
    void findSpecificStudyType() {
        assertAll(
                () -> assertThat(StudyType.from("online")).isEqualTo(ONLINE),
                () -> assertThat(StudyType.from("offline")).isEqualTo(OFFLINE)
        );
    }

    @Test
    @DisplayName("유효하지 않은 값으로 StudyTYpe을 조회할 수 없다")
    void throwExceptionByStudyTypeIsWeird() {
        assertThatThrownBy(() -> StudyType.from("anonymous"))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_TYPE_IS_WEIRD.getMessage());
    }
}
