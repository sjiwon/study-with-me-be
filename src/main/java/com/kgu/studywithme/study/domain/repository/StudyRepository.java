package com.kgu.studywithme.study.domain.repository;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Method Query
    boolean existsByNameValue(final String name);
}
