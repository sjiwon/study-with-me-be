package com.kgu.studywithme.member.application.usecase;

import com.kgu.studywithme.member.application.adapter.MemberInformationRepositoryAdapter;
import com.kgu.studywithme.member.application.usecase.query.QueryAttendanceRatioByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryGraduatedStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryParticipateStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryPublicInformationByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryReceivedReviewByIdUseCase;
import com.kgu.studywithme.member.infrastructure.query.dto.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.query.dto.GraduatedStudy;
import com.kgu.studywithme.member.infrastructure.query.dto.MemberPublicInformation;
import com.kgu.studywithme.member.infrastructure.query.dto.ParticipateStudy;
import com.kgu.studywithme.member.infrastructure.query.dto.ReceivedReview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPublicQueryService implements
        QueryPublicInformationByIdUseCase,
        QueryParticipateStudyByIdUseCase,
        QueryGraduatedStudyByIdUseCase,
        QueryReceivedReviewByIdUseCase,
        QueryAttendanceRatioByIdUseCase {

    private final MemberInformationRepositoryAdapter memberInformationRepositoryAdapter;

    @Override
    public MemberPublicInformation invoke(final QueryPublicInformationByIdUseCase.Query query) {
        return memberInformationRepositoryAdapter.fetchPublicInformationById(query.memberId());
    }

    @Override
    public List<ParticipateStudy> invoke(final QueryParticipateStudyByIdUseCase.Query query) {
        return memberInformationRepositoryAdapter.fetchParticipateStudyById(query.memberId());
    }

    @Override
    public List<GraduatedStudy> invoke(final QueryGraduatedStudyByIdUseCase.Query query) {
        return memberInformationRepositoryAdapter.fetchGraduatedStudyById(query.memberId());
    }

    @Override
    public List<ReceivedReview> invoke(final QueryReceivedReviewByIdUseCase.Query query) {
        return memberInformationRepositoryAdapter.fetchReceivedReviewById(query.memberId());
    }

    @Override
    public List<AttendanceRatio> invoke(final QueryAttendanceRatioByIdUseCase.Query query) {
        return memberInformationRepositoryAdapter.fetchAttendanceRatioById(query.memberId());
    }
}
