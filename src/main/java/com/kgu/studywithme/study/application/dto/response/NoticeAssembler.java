package com.kgu.studywithme.study.application.dto.response;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.NoticeInformation;

import java.util.List;

public record NoticeAssembler(
        List<NoticeInformation> result
) {
}
