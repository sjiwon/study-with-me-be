package com.kgu.studywithme.studyparticipant.domain.repository;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.LEAVE;

public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, Long> {
    // @Query
    @Query("""
            SELECT sp
            FROM StudyParticipant sp
            WHERE sp.study.id = :studyId AND sp.member.id = :memberId AND sp.status = :status
            """)
    Optional<StudyParticipant> findParticipantByStatus(
            @Param("studyId") final Long studyId,
            @Param("memberId") final Long memberId,
            @Param("status") final ParticipantStatus status
    );

    default StudyParticipant getApplier(final Long studyId, final Long memberId) {
        return findParticipantByStatus(studyId, memberId, APPLY)
                .orElseThrow(() -> StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND));
    }

    @Query("""
            SELECT sp.member
            FROM StudyParticipant sp
            WHERE sp.study.id = :studyId AND sp.status = :status
            """)
    List<Member> findParticipateMembersByStatus(
            @Param("studyId") final Long studyId,
            @Param("status") final ParticipantStatus status
    );

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE StudyParticipant sp
            SET sp.status = :status
            WHERE sp.study.id = :studyId AND sp.member.id = :memberId
            """)
    void updateParticipantStatus(
            @Param("studyId") final Long studyId,
            @Param("memberId") final Long memberId,
            @Param("status") final ParticipantStatus status
    );

    @Query("""
            SELECT sp.member.id
            FROM StudyParticipant sp
            WHERE sp.study.id = :studyId AND sp.status = :status
            """)
    List<Long> findMemberIdByStudyIdAndParticipantStatus(
            @Param("studyId") final Long studyId,
            @Param("status") final ParticipantStatus status
    );

    default boolean isParticipant(final Long studyId, final Long memberId) {
        return findMemberIdByStudyIdAndParticipantStatus(studyId, APPROVE).contains(memberId);
    }

    default boolean isGraduatedParticipant(final Long studyId, final Long memberId) {
        return findMemberIdByStudyIdAndParticipantStatus(studyId, GRADUATED).contains(memberId);
    }

    @Query("""
            SELECT sp.member.id
            FROM StudyParticipant sp
            WHERE sp.study.id = :studyId AND sp.status IN :statuses
            """)
    List<Long> findMemberIdByStudyIdAndParticipantStatusIn(
            @Param("studyId") final Long studyId,
            @Param("statuses") final List<ParticipantStatus> statuses
    );

    default boolean isApplierOrParticipant(final Long studyId, final Long memberId) {
        return findMemberIdByStudyIdAndParticipantStatusIn(studyId, List.of(APPLY, APPROVE)).contains(memberId);
    }

    default boolean isAlreadyLeaveOrGraduatedParticipant(final Long studyId, final Long memberId) {
        return findMemberIdByStudyIdAndParticipantStatusIn(studyId, List.of(LEAVE, GRADUATED)).contains(memberId);
    }
}
