package com.kgu.studywithme.study.domain.repository;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.model.RecruitmentStatus;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.ON;

public interface StudyRepository extends JpaRepository<Study, Long> {
    default Study getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }

    // @Query
    @Query("""
            SELECT s
            FROM Study s
            JOIN FETCH s.host
            WHERE s.id = :id
            """)
    Optional<Study> findByIdWithHost(@Param("id") final Long id);

    default Study getByIdWithHost(final Long id) {
        return findByIdWithHost(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }

    @Query("""
            SELECT s
            FROM Study s
            JOIN FETCH s.host
            WHERE s.id = :id AND s.recruitmentStatus = :status
            """)
    Optional<Study> findByIdAndRecruitmentStatusIs(@Param("id") final Long id, @Param("status") final RecruitmentStatus status);

    default Study getRecruitingStudy(final Long id) {
        return findByIdAndRecruitmentStatusIs(id, ON)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_IS_NOT_RECRUITING_NOW));
    }

    @Query("""
            SELECT s
            FROM Study s
            JOIN FETCH s.host
            WHERE s.id = :id AND s.terminated = FALSE
            """)
    Optional<Study> findByIdAndTerminatedFalse(@Param("id") final Long id);

    default Study getInProgressStudy(final Long id) {
        return findByIdAndTerminatedFalse(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_IS_TERMINATED));
    }

    @Query("""
            SELECT s.id
            FROM Study s
            WHERE s.name.value = :name
            """)
    Long findIdByNameUsed(@Param("name") final String name);

    default boolean isNameUsedByOther(final Long studyId, final String name) {
        final Long nameUsedId = findIdByNameUsed(name);
        return nameUsedId != null && !nameUsedId.equals(studyId);
    }

    @Query("""
            SELECT s.host.id
            FROM Study s
            WHERE s.id = :studyId
            """)
    Long getHostId(@Param("studyId") final Long studyId);

    default boolean isHost(final Long studyId, final Long memberId) {
        return getHostId(studyId).equals(memberId);
    }

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Study s SET s.favoriteCount = s.favoriteCount + 1 WHERE s.id = :id")
    void increaseFavoriteCount(@Param("id") Long id);

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Study s SET s.favoriteCount = s.favoriteCount - 1 WHERE s.id = :id")
    void decreaseFavoriteCount(@Param("id") Long id);

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Study s SET s.reviewCount = s.reviewCount + 1 WHERE s.id = :id")
    void increaseReviewCount(@Param("id") Long id);

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Study s SET s.reviewCount = s.reviewCount - 1 WHERE s.id = :id")
    void decreaseReviewCount(@Param("id") Long id);

    // Query Method
    boolean existsByNameValue(final String name);
}
