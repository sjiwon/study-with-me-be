package com.kgu.studywithme.studyattendance.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidAttendanceStatusValidator implements ConstraintValidator<ValidAttendanceStatus, String> {
    private static final List<String> ALLOWED_ATTENDANCE_STATUS = List.of(
            "출석",
            "지각",
            "결석"
    );

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if ("미출결".equals(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("출석을 미출결로 수정할 수 없습니다.")
                    .addConstraintViolation();
            return false;
        }

        return ALLOWED_ATTENDANCE_STATUS.contains(value);
    }
}
