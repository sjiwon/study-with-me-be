package com.kgu.studywithme.study.infrastructure.query.dto;

import java.util.List;

public record StudyParticipantInformation(
        StudyMember host,
        List<StudyMember> participants
) {
}
