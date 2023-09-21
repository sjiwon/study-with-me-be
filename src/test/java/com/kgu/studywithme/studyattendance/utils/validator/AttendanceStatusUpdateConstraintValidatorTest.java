package com.kgu.studywithme.studyattendance.utils.validator;

import com.kgu.studywithme.common.ParallelTest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("StudyAttendance -> AttendanceStatusUpdateConstraintValidator 테스트")
class AttendanceStatusUpdateConstraintValidatorTest extends ParallelTest {
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private final ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    private final AttendanceStatusUpdateConstraintValidator sut = new AttendanceStatusUpdateConstraintValidator();

    @Test
    @DisplayName("status가 미출결이면 validator를 통과하지 못한다")
    void nonAttendanceStatus() {
        // given
        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builder);
        given(builder.addConstraintViolation()).willReturn(context);

        // when
        final boolean actual = sut.isValid("미출결", context);

        // then
        assertAll(
                () -> verify(context).disableDefaultConstraintViolation(),
                () -> verify(context).buildConstraintViolationWithTemplate("출석을 미출결로 수정할 수 없습니다."),
                () -> verify(builder).addConstraintViolation(),
                () -> assertThat(actual).isFalse()
        );
    }

    @Test
    @DisplayName("status가 출석/지각/결석 중 하나가 아니면 validator를 통과하지 못한다")
    void unknownStatus() {
        // given
        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builder);
        given(builder.addConstraintViolation()).willReturn(context);

        // when
        final boolean actual = sut.isValid("??", context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("status가 출석/지각/결석 중 하나면 validator를 통과한다")
    void success() {
        // when
        final boolean actual1 = sut.isValid("출석", context);
        final boolean actual2 = sut.isValid("지각", context);
        final boolean actual3 = sut.isValid("결석", context);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue()
        );
    }
}
