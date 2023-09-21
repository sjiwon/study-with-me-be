package com.kgu.studywithme.memberreview.domain.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import com.kgu.studywithme.studyattendance.domain.repository.query.StudyAttendanceMetadataRepository;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.StudyAttendanceWeekly;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("MemberReview -> MemberReviewInspector 테스트")
public class MemberReviewInspectorTest {
    private final StudyAttendanceMetadataRepository studyAttendanceMetadataRepository = mock(StudyAttendanceMetadataRepository.class);
    private final MemberReviewRepository memberReviewRepository = mock(MemberReviewRepository.class);
    private final MemberReviewInspector sut = new MemberReviewInspector(studyAttendanceMetadataRepository, memberReviewRepository);

    private final Member memberA = JIWON.toMember().apply(1L);
    private final Member memberB = GHOST.toMember().apply(2L);

    @Test
    @DisplayName("본인에게 리뷰를 남길 수 없다")
    void throwExceptionBySelfReviewNotAllowed() {
        assertThatThrownBy(() -> sut.checkReviewerHasEligibilityToReview(memberA.getId(), memberA.getId()))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED.getMessage());

        assertAll(
                () -> verify(studyAttendanceMetadataRepository, times(0)).findMemberParticipateWeekly(memberA.getId()),
                () -> verify(studyAttendanceMetadataRepository, times(0)).findMemberParticipateWeekly(memberB.getId()),
                () -> verify(memberReviewRepository, times(0)).existsByReviewerIdAndRevieweeId(memberA.getId(), memberB.getId())
        );
    }

    @Test
    @DisplayName("같이 스터디한 기록이 없다면 Reviewer는 Reviewee에게 리뷰를 작성할 자격이 없다")
    void throwExceptionByCommonStudyRecordNotFound() {
        // given
        given(studyAttendanceMetadataRepository.findMemberParticipateWeekly(memberA.getId()))
                .willReturn(List.of(
                        new StudyAttendanceWeekly(1L, 1),
                        new StudyAttendanceWeekly(1L, 2),
                        new StudyAttendanceWeekly(1L, 3)
                ));
        given(studyAttendanceMetadataRepository.findMemberParticipateWeekly(memberB.getId()))
                .willReturn(List.of(
                        new StudyAttendanceWeekly(2L, 1),
                        new StudyAttendanceWeekly(2L, 2),
                        new StudyAttendanceWeekly(2L, 3)
                ));

        // when - then
        assertThatThrownBy(() -> sut.checkReviewerHasEligibilityToReview(memberA.getId(), memberB.getId()))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberA.getId()),
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberB.getId()),
                () -> verify(memberReviewRepository, times(0)).existsByReviewerIdAndRevieweeId(memberA.getId(), memberB.getId())
        );
    }

    @Test
    @DisplayName("이미 리뷰를 작성했다면 2번 이상 리뷰를 작성할 수 없다")
    void throwExceptionByAlreadyReview() {
        // given
        given(studyAttendanceMetadataRepository.findMemberParticipateWeekly(memberA.getId()))
                .willReturn(List.of(
                        new StudyAttendanceWeekly(1L, 1),
                        new StudyAttendanceWeekly(1L, 2),
                        new StudyAttendanceWeekly(1L, 3)
                ));
        given(studyAttendanceMetadataRepository.findMemberParticipateWeekly(memberB.getId()))
                .willReturn(List.of(
                        new StudyAttendanceWeekly(1L, 1),
                        new StudyAttendanceWeekly(2L, 2),
                        new StudyAttendanceWeekly(2L, 3)
                ));
        given(memberReviewRepository.existsByReviewerIdAndRevieweeId(memberA.getId(), memberB.getId())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.checkReviewerHasEligibilityToReview(memberA.getId(), memberB.getId()))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.ALREADY_REVIEW.getMessage());

        assertAll(
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberA.getId()),
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberB.getId()),
                () -> verify(memberReviewRepository, times(1)).existsByReviewerIdAndRevieweeId(memberA.getId(), memberB.getId())
        );
    }

    @Test
    @DisplayName("리뷰 자격 검증을 통과한다")
    void success() {
        // given
        given(studyAttendanceMetadataRepository.findMemberParticipateWeekly(memberA.getId()))
                .willReturn(List.of(
                        new StudyAttendanceWeekly(1L, 1),
                        new StudyAttendanceWeekly(1L, 2),
                        new StudyAttendanceWeekly(1L, 3)
                ));
        given(studyAttendanceMetadataRepository.findMemberParticipateWeekly(memberB.getId()))
                .willReturn(List.of(
                        new StudyAttendanceWeekly(1L, 1),
                        new StudyAttendanceWeekly(2L, 2),
                        new StudyAttendanceWeekly(2L, 3)
                ));
        given(memberReviewRepository.existsByReviewerIdAndRevieweeId(memberA.getId(), memberB.getId())).willReturn(false);

        // when - then
        assertDoesNotThrow(() -> sut.checkReviewerHasEligibilityToReview(memberA.getId(), memberB.getId()));

        assertAll(
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberA.getId()),
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberB.getId()),
                () -> verify(memberReviewRepository, times(1)).existsByReviewerIdAndRevieweeId(memberA.getId(), memberB.getId())
        );
    }
}
