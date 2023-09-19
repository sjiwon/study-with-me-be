package com.kgu.studywithme.study.domain.repository;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.model.RecruitmentStatus;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
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
    @Query("SELECT s.id" +
            " FROM Study s" +
            " WHERE s.name.value = :name")
    Long findIdByNameUsed(@Param("name") final String name);

    @Query("SELECT s.hostId" +
            " FROM Study s" +
            " WHERE s.id = :studyId")
    Long getHostId(@Param("studyId") final Long studyId);

    default boolean isHost(final Long studyId, final Long memberId) {
        return getHostId(studyId).equals(memberId);
    }

    // Query Method
    boolean existsByNameValue(final String name);

    Optional<Study> findByIdAndRecruitmentStatusIs(final Long id, final RecruitmentStatus status);

    default Study getRecruitingStudy(final Long id) {
        return findByIdAndRecruitmentStatusIs(id, ON)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_IS_NOT_RECRUITING_NOW));
    }

    Optional<Study> findByIdAndTerminatedFalse(final Long id);

    default Study getInProgressStudy(final Long id) {
        return findByIdAndTerminatedFalse(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_IS_TERMINATED));
    }
}
