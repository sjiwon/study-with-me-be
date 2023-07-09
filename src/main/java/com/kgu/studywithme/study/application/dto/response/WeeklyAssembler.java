package com.kgu.studywithme.study.application.dto.response;

import java.util.List;

public record WeeklyAssembler(
        List<WeeklySummary> weeks
) {
}
