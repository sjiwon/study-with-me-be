package com.kgu.studywithme.member.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberCommand;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.domain.service.MemberResourceValidator;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> SignUpMemberUseCase 테스트")
class SignUpMemberUseCaseTest extends UseCaseTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final MemberResourceValidator memberResourceValidator = new MemberResourceValidator(memberRepository);
    private final SignUpMemberUseCase sut = new SignUpMemberUseCase(memberResourceValidator, memberRepository);

    private final SignUpMemberCommand command = new SignUpMemberCommand(
            JIWON.getName(),
            JIWON.getNickname(),
            JIWON.getEmail(),
            JIWON.getBirth(),
            JIWON.getPhone(),
            JIWON.getGender(),
            JIWON.getAddress(),
            JIWON.getInterests()
    );
    private final Member member = command.toDomain().apply(1L);

    @Test
    @DisplayName("이미 사용하고 있는 이메일이면 회원가입에 실패한다")
    void throwExceptionByDuplicateEmail() {
        // given
        given(memberRepository.existsByEmailValue(command.email().getValue())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).existsByEmailValue(command.email().getValue()),
                () -> verify(memberRepository, times(0)).existsByNicknameValue(command.nickname().getValue()),
                () -> verify(memberRepository, times(0)).existsByPhoneValue(command.phone().getValue()),
                () -> verify(memberRepository, times(0)).save(any(Member.class))
        );
    }

    @Test
    @DisplayName("이미 사용하고 있는 닉네임이면 회원가입에 실패한다")
    void throwExceptionByDuplicateNickname() {
        // given
        given(memberRepository.existsByEmailValue(command.email().getValue())).willReturn(false);
        given(memberRepository.existsByNicknameValue(command.nickname().getValue())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).existsByEmailValue(command.email().getValue()),
                () -> verify(memberRepository, times(1)).existsByNicknameValue(command.nickname().getValue()),
                () -> verify(memberRepository, times(0)).existsByPhoneValue(command.phone().getValue()),
                () -> verify(memberRepository, times(0)).save(any(Member.class))
        );
    }

    @Test
    @DisplayName("이미 사용하고 있는 전화번호면 회원가입에 실패한다")
    void throwExceptionByDuplicatePhone() {
        // given
        given(memberRepository.existsByEmailValue(command.email().getValue())).willReturn(false);
        given(memberRepository.existsByNicknameValue(command.nickname().getValue())).willReturn(false);
        given(memberRepository.existsByPhoneValue(command.phone().getValue())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).existsByEmailValue(command.email().getValue()),
                () -> verify(memberRepository, times(1)).existsByNicknameValue(command.nickname().getValue()),
                () -> verify(memberRepository, times(1)).existsByPhoneValue(command.phone().getValue()),
                () -> verify(memberRepository, times(0)).save(any(Member.class))
        );
    }

    @Test
    @DisplayName("모든 중복 검사를 통과한다면 회원가입에 성공한다")
    void success() {
        // given
        given(memberRepository.existsByEmailValue(command.email().getValue())).willReturn(false);
        given(memberRepository.existsByNicknameValue(command.nickname().getValue())).willReturn(false);
        given(memberRepository.existsByPhoneValue(command.phone().getValue())).willReturn(false);
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // when
        final Long savedMemberId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).existsByEmailValue(command.email().getValue()),
                () -> verify(memberRepository, times(1)).existsByNicknameValue(command.nickname().getValue()),
                () -> verify(memberRepository, times(1)).existsByPhoneValue(command.phone().getValue()),
                () -> verify(memberRepository, times(1)).save(any(Member.class)),
                () -> assertThat(savedMemberId).isEqualTo(member.getId())
        );
    }
}
