package com.kgu.studywithme.study.application.adapter;

public interface StudyDuplicateCheckRepositoryAdapter {
    boolean isNameExists(final String name);

    boolean isNameUsedByOther(final Long studyId, final String name);
}
