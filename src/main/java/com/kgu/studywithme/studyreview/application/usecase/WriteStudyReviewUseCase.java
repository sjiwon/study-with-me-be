package com.kgu.studywithme.studyreview.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewCommand;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.domain.repository.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WriteStudyReviewUseCase {
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyReviewRepository studyReviewRepository;

    @StudyWithMeWritableTransactional
    public Long invoke(final WriteStudyReviewCommand command) {
        final Study study = studyRepository.getById(command.studyId());
        final Member member = memberRepository.getById(command.memberId());

        validateMemberIsGraduatedStudy(study, member);
        validateAlreadyWritten(study, member);
        studyRepository.increaseReviewCount(study.getId());
        return studyReviewRepository.save(StudyReview.writeReview(study, member, command.content())).getId();
    }

    private void validateMemberIsGraduatedStudy(final Study study, final Member member) {
        if (!studyParticipantRepository.isGraduatedParticipant(study.getId(), member.getId())) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW);
        }
    }

    private void validateAlreadyWritten(final Study study, final Member member) {
        if (studyReviewRepository.existsByStudyIdAndWriterId(study.getId(), member.getId())) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ALREADY_WRITTEN);
        }
    }
}
