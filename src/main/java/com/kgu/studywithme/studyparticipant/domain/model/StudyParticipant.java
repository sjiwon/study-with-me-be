package com.kgu.studywithme.studyparticipant.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "study_participant",
        indexes = {
                @Index(name = "idx_study_participant_member_id_status", columnList = "member_id, status"),
                @Index(name = "idx_study_participant_study_id_member_id_status", columnList = "study_id, member_id, status"),
                @Index(name = "idx_study_participant_study_id_status", columnList = "study_id, status")
        }
)
public class StudyParticipant extends BaseEntity<StudyParticipant> {
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipantStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Builder
    private StudyParticipant(final Study study, final Member member, final ParticipantStatus status) {
        this.study = study;
        this.member = member;
        this.status = status;
    }

    public static StudyParticipant applyInStudy(final Study study, final Member member) {
        return new StudyParticipant(study, member, APPLY);
    }

    public static StudyParticipant applyHost(final Study study, final Member host) {
        return new StudyParticipant(study, host, APPROVE);
    }

    public static StudyParticipant applyParticipant(final Study study, final Member member, final ParticipantStatus status) {
        return new StudyParticipant(study, member, status);
    }
}
