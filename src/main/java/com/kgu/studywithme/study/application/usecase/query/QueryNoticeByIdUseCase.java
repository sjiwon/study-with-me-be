package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.NoticeInformation;

import java.util.List;

public interface QueryNoticeByIdUseCase {
    List<NoticeInformation> queryNotice(final Query query);

    record Query(
            Long studyId
    ) {
    }
}
