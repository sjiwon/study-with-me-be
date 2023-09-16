package com.kgu.studywithme.memberreview.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewCommand;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.domain.service.MemberReviewValidator;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("MemberReview -> WriteMemberReviewUseCase 테스트")
class WriteMemberReviewUseCaseTest extends UseCaseTest {
    private final MemberReviewValidator memberReviewValidator = mock(MemberReviewValidator.class);
    private final MemberReviewRepository memberReviewRepository = mock(MemberReviewRepository.class);
    private final WriteMemberReviewUseCase sut = new WriteMemberReviewUseCase(memberReviewValidator, memberReviewRepository);

    private final Member memberA = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member memberB = JIWON.toMember().apply(2L, LocalDateTime.now());
    private final WriteMemberReviewCommand selfReviewedCommand = new WriteMemberReviewCommand(memberA.getId(), memberA.getId(), "Good!!");
    private final WriteMemberReviewCommand command = new WriteMemberReviewCommand(memberA.getId(), memberB.getId(), "Good!!");

    @Test
    @DisplayName("본인에게 리뷰를 남길 수 없다")
    void throwExceptionBySelfReviewNotAllowed() {
        // given
        doThrow(StudyWithMeException.type(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED))
                .when(memberReviewValidator)
                .validateReviewEligibility(memberA.getId(), memberA.getId());

        // when - then
        assertThatThrownBy(() -> sut.invoke(selfReviewedCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED.getMessage());

        assertAll(
                () -> verify(memberReviewValidator, times(1)).validateReviewEligibility(memberA.getId(), memberA.getId()),
                () -> verify(memberReviewRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("함께 스터디를 진행한 기록이 없다면 리뷰를 남길 수 없다")
    void throwExceptionByCommonStudyRecordNotFound() {
        // given
        doThrow(StudyWithMeException.type(MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND))
                .when(memberReviewValidator)
                .validateReviewEligibility(memberA.getId(), memberB.getId());

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(memberReviewValidator, times(1)).validateReviewEligibility(memberA.getId(), memberB.getId()),
                () -> verify(memberReviewRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("해당 사용자에 대해 2번 이상 리뷰를 남길 수 없다")
    void throwExceptionByAlreadyReview() {
        // given
        doThrow(StudyWithMeException.type(MemberReviewErrorCode.ALREADY_REVIEW))
                .when(memberReviewValidator)
                .validateReviewEligibility(memberA.getId(), memberB.getId());

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.ALREADY_REVIEW.getMessage());

        assertAll(
                () -> verify(memberReviewValidator, times(1)).validateReviewEligibility(memberA.getId(), memberB.getId()),
                () -> verify(memberReviewRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("리뷰를 작성한다")
    void success() {
        // given
        doNothing()
                .when(memberReviewValidator)
                .validateReviewEligibility(memberA.getId(), memberB.getId());

        final MemberReview memberReview = command.toDomain().apply(1L);
        given(memberReviewRepository.save(any())).willReturn(memberReview);

        // when
        final Long memberReviewId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(memberReviewValidator, times(1)).validateReviewEligibility(memberA.getId(), memberB.getId()),
                () -> verify(memberReviewRepository, times(1)).save(any()),
                () -> assertThat(memberReviewId).isEqualTo(memberReview.getId())
        );
    }
}
