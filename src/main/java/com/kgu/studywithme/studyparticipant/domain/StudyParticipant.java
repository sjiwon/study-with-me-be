package com.kgu.studywithme.studyparticipant.domain;

import com.kgu.studywithme.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_participant")
public class StudyParticipant extends BaseEntity<StudyParticipant> {
    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipantStatus status;

    @Builder
    private StudyParticipant(
            final Long studyId,
            final Long memberId,
            final ParticipantStatus status
    ) {
        this.studyId = studyId;
        this.memberId = memberId;
        this.status = status;
    }

    public static StudyParticipant applyInStudy(
            final Long studyId,
            final Long memberId
    ) {
        return new StudyParticipant(studyId, memberId, APPLY);
    }

    public static StudyParticipant applyHost(
            final Long studyId,
            final Long hostId
    ) {
        return new StudyParticipant(studyId, hostId, APPROVE);
    }

    public static StudyParticipant applyParticipant(
            final Long studyId,
            final Long hostId,
            final ParticipantStatus status
    ) {
        return new StudyParticipant(studyId, hostId, status);
    }

    public void updateStatus(final ParticipantStatus status) {
        this.status = status;
    }
}
