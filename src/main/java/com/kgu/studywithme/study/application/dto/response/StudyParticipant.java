package com.kgu.studywithme.study.application.dto.response;

import java.util.List;

public record StudyParticipant(
        StudyMember host,
        List<StudyMember> participants
) {
}
