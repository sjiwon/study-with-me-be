package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.infrastructure.query.dto.NoticeInformation;

import java.util.List;

public interface QueryNoticeByIdUseCase {
    List<NoticeInformation> invoke(final Query query);

    record Query(
            Long studyId
    ) {
    }
}
