package com.kgu.studywithme.memberreview.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewCommand;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.domain.service.MemberReviewInspector;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import com.kgu.studywithme.studyattendance.domain.repository.query.StudyAttendanceMetadataRepository;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.StudyAttendanceWeekly;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("MemberReview -> WriteMemberReviewUseCase 테스트")
class WriteMemberReviewUseCaseTest extends UseCaseTest {
    private final MemberReviewRepository memberReviewRepository = mock(MemberReviewRepository.class);
    private final StudyAttendanceMetadataRepository studyAttendanceMetadataRepository = mock(StudyAttendanceMetadataRepository.class);
    private final MemberReviewInspector memberReviewInspector = new MemberReviewInspector(studyAttendanceMetadataRepository, memberReviewRepository);
    private final WriteMemberReviewUseCase sut = new WriteMemberReviewUseCase(memberReviewInspector, memberReviewRepository);

    private final Member memberA = JIWON.toMember().apply(1L);
    private final Member memberB = JIWON.toMember().apply(2L);
    private final WriteMemberReviewCommand selfReviewedCommand = new WriteMemberReviewCommand(memberA.getId(), memberA.getId(), "Good!!");
    private final WriteMemberReviewCommand command = new WriteMemberReviewCommand(memberA.getId(), memberB.getId(), "Good!!");

    @Test
    @DisplayName("본인에게 리뷰를 남길 수 없다")
    void throwExceptionBySelfReviewNotAllowed() {
        assertThatThrownBy(() -> sut.invoke(selfReviewedCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED.getMessage());

        assertAll(
                () -> verify(studyAttendanceMetadataRepository, times(0)).findMemberParticipateWeekly(memberA.getId()),
                () -> verify(studyAttendanceMetadataRepository, times(0)).findMemberParticipateWeekly(memberB.getId()),
                () -> verify(memberReviewRepository, times(0)).existsByReviewerIdAndRevieweeId(memberA.getId(), memberB.getId()),
                () -> verify(memberReviewRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("함께 스터디를 진행한 기록이 없다면 리뷰를 남길 수 없다")
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
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberA.getId()),
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberB.getId()),
                () -> verify(memberReviewRepository, times(0)).existsByReviewerIdAndRevieweeId(memberA.getId(), memberB.getId()),
                () -> verify(memberReviewRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("해당 사용자에 대해 2번 이상 리뷰를 남길 수 없다")
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
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.ALREADY_REVIEW.getMessage());

        assertAll(
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberA.getId()),
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberB.getId()),
                () -> verify(memberReviewRepository, times(1)).existsByReviewerIdAndRevieweeId(memberA.getId(), memberB.getId()),
                () -> verify(memberReviewRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("리뷰를 작성한다")
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

        final MemberReview memberReview = command.toDomain().apply(1L);
        given(memberReviewRepository.save(any())).willReturn(memberReview);

        // when
        final Long memberReviewId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberA.getId()),
                () -> verify(studyAttendanceMetadataRepository, times(1)).findMemberParticipateWeekly(memberB.getId()),
                () -> verify(memberReviewRepository, times(1)).existsByReviewerIdAndRevieweeId(memberA.getId(), memberB.getId()),
                () -> verify(memberReviewRepository, times(1)).save(any()),
                () -> assertThat(memberReviewId).isEqualTo(memberReview.getId())
        );
    }
}
