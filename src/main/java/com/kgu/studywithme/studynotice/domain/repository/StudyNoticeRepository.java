package com.kgu.studywithme.studynotice.domain.repository;

import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyNoticeRepository extends JpaRepository<StudyNotice, Long> {
}
