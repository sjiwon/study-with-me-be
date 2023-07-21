package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> 도메인 [StudyType VO] 테스트")
class StudyTypeTest {
    @Test
    @DisplayName("스터디 유형을 조회한다")
    void findSpecificStudyType() {
        assertAll(
                () -> assertThat(StudyType.from("online")).isEqualTo(ONLINE),
                () -> assertThat(StudyType.from("offline")).isEqualTo(OFFLINE)
        );
    }

    @Test
    @DisplayName("이상한 단어로 스터디 유형을 조회할 수 없다")
    void throwExceptionByStudyTypeIsWeird() {
        assertThatThrownBy(() -> StudyType.from("anonymous"))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_TYPE_IS_WEIRD.getMessage());
    }
}
