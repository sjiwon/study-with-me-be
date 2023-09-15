package com.kgu.studywithme.member.domain.model;

import com.kgu.studywithme.common.ExecuteParallel;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.member.domain.model.Gender.FEMALE;
import static com.kgu.studywithme.member.domain.model.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExecuteParallel
@DisplayName("Member -> 도메인 [Gender VO] 테스트")
class GenderTest {
    @Test
    @DisplayName("유효하지 않은 성별(M or F 이외)을 조회하면 예외가 발생한다")
    void throwExceptionByInvalidGender() {
        assertThatThrownBy(() -> Gender.from("T"))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_GENDER.getMessage());
    }

    @Test
    @DisplayName("유효한 성별을 조회한다")
    void success() {
        assertAll(
                () -> assertThat(Gender.from("M")).isEqualTo(MALE),
                () -> assertThat(Gender.from("F")).isEqualTo(FEMALE)
        );
    }
}
