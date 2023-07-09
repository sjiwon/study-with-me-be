package com.kgu.studywithme.study.application.dto.response;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.BasicStudy;

import java.util.List;

public record DefaultStudyResponse(
        List<BasicStudy> studyList,
        boolean hasNext
) {
}
