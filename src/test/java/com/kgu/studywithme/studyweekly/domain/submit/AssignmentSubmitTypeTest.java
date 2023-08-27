package com.kgu.studywithme.studyweekly.domain.submit;

import com.kgu.studywithme.common.ExecuteParallel;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.FILE;
import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExecuteParallel
@DisplayName("StudyWeekly/Submit -> 도메인 [AssignmentSubmitType VO] 테스트")
public class AssignmentSubmitTypeTest {
    @Test
    @DisplayName("link/file 이외의 제출 타입은 제공하지 않는다")
    void throwExceptionByInvalidSubmitType() {
        assertThatThrownBy(() -> AssignmentSubmitType.from("linkfile"))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.INVALID_SUBMIT_TYPE.getMessage());
    }

    @Test
    @DisplayName("제출 타입에 대한 AssignmentSubmitType를 얻는다")
    void success() {
        assertAll(
                () -> assertThat(AssignmentSubmitType.from("link")).isEqualTo(LINK),
                () -> assertThat(AssignmentSubmitType.from("file")).isEqualTo(FILE)
        );
    }
}
