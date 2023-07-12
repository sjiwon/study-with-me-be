package com.kgu.studywithme.study.application.dto.response;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.StudyApplicantInformation;

import java.util.List;

public record StudyApplicant(
        List<StudyApplicantInformation> applicants
) {
}