package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class GraduationPolicy {
    private static final int DEFAULT_UPDATE_CHANCE = 3;

    @Column(name = "minimum_attendance", nullable = false)
    private int minimumAttendance;

    @Column(name = "policy_update_chance", nullable = false)
    private int updateChance;

    private GraduationPolicy(
            final int minimumAttendance,
            final int updateChance
    ) {
        this.minimumAttendance = minimumAttendance;
        this.updateChance = updateChance;
    }

    public static GraduationPolicy initPolicy(final int minimumAttendance) {
        return new GraduationPolicy(minimumAttendance, DEFAULT_UPDATE_CHANCE);
    }

    public GraduationPolicy update(final int minimumAttendance) {
        if (this.minimumAttendance == minimumAttendance) {
            return new GraduationPolicy(minimumAttendance, updateChance);
        }

        validateUpdateChangeIsRemain();
        return new GraduationPolicy(minimumAttendance, updateChance - 1);
    }

    private void validateUpdateChangeIsRemain() {
        if (updateChance == 0) {
            throw StudyWithMeException.type(StudyErrorCode.NO_CHANGE_TO_UPDATE_GRADUATION_POLICY);
        }
    }

    public GraduationPolicy resetUpdateChanceByDelegatingHostAuthority() {
        return new GraduationPolicy(minimumAttendance, DEFAULT_UPDATE_CHANCE);
    }

    public boolean isGraduationRequirementsFulfilled(final int value) {
        return minimumAttendance <= value;
    }
}
