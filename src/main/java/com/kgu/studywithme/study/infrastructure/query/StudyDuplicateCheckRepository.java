package com.kgu.studywithme.study.infrastructure.query;

public interface StudyDuplicateCheckRepository {
    boolean isNameExists(final String name);

    boolean isNameUsedByOther(final Long studyId, final String name);
}
