package com.kgu.studywithme.study.application.usecase.dto;

import com.kgu.studywithme.study.domain.repository.query.dto.StudyPreview;

import java.util.List;

public record StudyPagingResponse(
        List<StudyPreview> studies,
        boolean hasNext
) {
}
