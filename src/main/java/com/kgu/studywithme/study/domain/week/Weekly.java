package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Weekly {
    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Week> weeks = new ArrayList<>();

    public static Weekly createWeeklyPage() {
        return new Weekly();
    }

    public void registerWeek(final Week week) {
        validateUniqueWeek(week.getWeek());
        weeks.add(week);
    }

    private void validateUniqueWeek(final int week) {
        if (isAlreadyExistsPerWeek(week)) {
            throw StudyWithMeException.type(StudyErrorCode.ALREADY_WEEK_CREATED);
        }
    }

    private boolean isAlreadyExistsPerWeek(final int week) {
        return weeks.stream()
                .anyMatch(weekInfo -> weekInfo.getWeek() == week);
    }

    public int getCount() {
        return weeks.size();
    }
}
