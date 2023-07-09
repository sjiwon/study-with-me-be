package com.kgu.studywithme.member.application.dto.response;

import com.kgu.studywithme.study.infra.query.dto.response.SimpleGraduatedStudy;

import java.util.List;

public record GraduatedStudy(
        List<SimpleGraduatedStudy> result
) {
}
