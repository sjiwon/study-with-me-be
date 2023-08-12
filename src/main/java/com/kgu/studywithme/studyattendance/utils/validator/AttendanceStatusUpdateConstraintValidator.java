package com.kgu.studywithme.studyattendance.utils.validator;

import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.stream.Stream;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;

public class AttendanceStatusUpdateConstraintValidator implements ConstraintValidator<AttendanceStatusUpdateConstraint, String> {
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (NON_ATTENDANCE.getValue().equals(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("출석을 미출결로 수정할 수 없습니다.")
                    .addConstraintViolation();
            return false;
        }

        return Stream.of(ATTENDANCE, LATE, ABSENCE)
                .map(AttendanceStatus::getValue)
                .toList()
                .contains(value);
    }
}
