package com.kgu.studywithme.study.application.dto;

import com.kgu.studywithme.study.infrastructure.query.dto.StudyPreview;

import java.util.List;

public record StudyPagingResponse(
        List<StudyPreview> studies,
        boolean hasNext
) {
}
