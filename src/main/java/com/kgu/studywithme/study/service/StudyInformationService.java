package com.kgu.studywithme.study.service;

import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;
import com.kgu.studywithme.study.infra.query.dto.response.ReviewInformation;
import com.kgu.studywithme.study.infra.query.dto.response.StudyApplicantInformation;
import com.kgu.studywithme.study.service.dto.response.NoticeAssembler;
import com.kgu.studywithme.study.service.dto.response.ReviewAssembler;
import com.kgu.studywithme.study.service.dto.response.StudyApplicant;
import com.kgu.studywithme.study.service.dto.response.StudyInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyInformationService {
    private final StudyFindService studyFindService;
    private final StudyRepository studyRepository;

    public StudyInformation getInformation(Long studyId) {
        Study study = studyFindService.findByIdWithHashtags(studyId);
        return new StudyInformation(study);
    }

    public ReviewAssembler getReviews(Long studyId) {
        int graduateCount = studyRepository.getGraduatedParticipantCountByStudyId(studyId);
        List<ReviewInformation> reviews = studyRepository.findReviewByStudyId(studyId);

        return new ReviewAssembler(graduateCount, reviews);
    }

    public NoticeAssembler getNotices(Long studyId) {
        List<NoticeInformation> result = studyRepository.findNoticeWithCommentsByStudyId(studyId);
        return new NoticeAssembler(result);
    }

    public StudyApplicant getApplicants(Long studyId) {
        List<StudyApplicantInformation> result = studyRepository.findApplicantByStudyId(studyId);
        return new StudyApplicant(result);
    }
}