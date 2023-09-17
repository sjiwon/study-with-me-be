package com.kgu.studywithme.member.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.usecase.command.UpdateMemberCommand;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.domain.service.MemberResourceValidator;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> UpdateMemberUseCase 테스트")
class UpdateMemberUseCaseTest extends UseCaseTest {
    private final MemberResourceValidator memberResourceValidator = mock(MemberResourceValidator.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final UpdateMemberUseCase sut = new UpdateMemberUseCase(memberResourceValidator, memberRepository);

    private final Member member = JIWON.toMember().apply(1L);
    private final UpdateMemberCommand command = new UpdateMemberCommand(
            member.getId(),
            GHOST.getNickname(),
            GHOST.getPhone(),
            GHOST.getAddress(),
            false,
            GHOST.getInterests()
    );

    @Test
    @DisplayName("다른 사람이 사용하고 있는 닉네임으로 수정할 수 없다")
    void throwExceptionByDuplicateNickname() {
        // given
        doThrow(StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME))
                .when(memberResourceValidator)
                .validateInUpdate(member.getId(), command.nickname(), command.phone());

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

        assertAll(
                () -> verify(memberResourceValidator, times(1)).validateInUpdate(member.getId(), command.nickname(), command.phone()),
                () -> verify(memberRepository, times(0)).getById(command.memberId())
        );
    }

    @Test
    @DisplayName("다른 사람이 사용하고 있는 전화번호로 수정할 수 없다")
    void throwExceptionByDuplicatePhone() {
        // given
        doThrow(StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE))
                .when(memberResourceValidator)
                .validateInUpdate(member.getId(), command.nickname(), command.phone());

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

        assertAll(
                () -> verify(memberResourceValidator, times(1)).validateInUpdate(member.getId(), command.nickname(), command.phone()),
                () -> verify(memberRepository, times(0)).getById(command.memberId())
        );
    }

    @Test
    @DisplayName("사용자 정보를 수정한다")
    void success() {
        // given
        given(memberRepository.getById(command.memberId())).willReturn(member);
        doNothing()
                .when(memberResourceValidator)
                .validateInUpdate(member.getId(), command.nickname(), command.phone());

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(memberResourceValidator, times(1)).validateInUpdate(member.getId(), command.nickname(), command.phone()),
                () -> verify(memberRepository, times(1)).getById(command.memberId()),
                () -> assertThat(member.getNickname().getValue()).isEqualTo(command.nickname().getValue()),
                () -> assertThat(member.getPhone().getValue()).isEqualTo(command.phone().getValue()),
                () -> assertThat(member.getAddress().getProvince()).isEqualTo(command.address().getProvince()),
                () -> assertThat(member.getAddress().getCity()).isEqualTo(command.address().getCity()),
                () -> assertThat(member.isEmailOptIn()).isEqualTo(command.emailOptIn()),
                () -> assertThat(member.getInterests()).containsExactlyInAnyOrderElementsOf(command.interests())
        );
    }
}
