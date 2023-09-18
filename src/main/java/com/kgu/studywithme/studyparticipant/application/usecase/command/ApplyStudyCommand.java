package com.kgu.studywithme.studyparticipant.application.usecase.command;

import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;

public record ApplyStudyCommand(
        Long studyId,
        Long applierId
) {
    public StudyParticipant toDomain() {
        return StudyParticipant.applyInStudy(studyId, applierId);
    }
}
