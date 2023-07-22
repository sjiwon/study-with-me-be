package com.kgu.studywithme.study.infrastructure.repository.query;

public interface StudyDuplicateCheckRepository {
    boolean isNameExists(final String name);

    boolean isNameUsedByOther(final Long studyId, final String name);
}
