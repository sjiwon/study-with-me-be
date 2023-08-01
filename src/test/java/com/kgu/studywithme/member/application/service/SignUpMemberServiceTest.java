package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberUseCase;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> SignUpMemberService 테스트")
class SignUpMemberServiceTest extends UseCaseTest {
    @InjectMocks
    private SignUpMemberService signUpMemberService;

    @Mock
    private MemberRepository memberRepository;

    private final SignUpMemberUseCase.Command command =
            new SignUpMemberUseCase.Command(
                    JIWON.getName(),
                    JIWON.getNickname(),
                    JIWON.getEmail(),
                    JIWON.getBirth(),
                    JIWON.getPhone(),
                    JIWON.getGender(),
                    JIWON.getRegion(),
                    true,
                    JIWON.getInterests()
            );

    @Test
    @DisplayName("이미 사용하고 있는 이메일이면 회원가입에 실패한다")
    void throwExceptionByDuplicateEmail() {
        // given
        given(memberRepository.isEmailExists(any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> signUpMemberService.signUp(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).isEmailExists(any()),
                () -> verify(memberRepository, times(0)).isNicknameExists(any()),
                () -> verify(memberRepository, times(0)).isPhoneExists(any()),
                () -> verify(memberRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("이미 사용하고 있는 닉네임이면 회원가입에 실패한다")
    void throwExceptionByDuplicateNickname() {
        // given
        given(memberRepository.isEmailExists(any())).willReturn(false);
        given(memberRepository.isNicknameExists(any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> signUpMemberService.signUp(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).isEmailExists(any()),
                () -> verify(memberRepository, times(1)).isNicknameExists(any()),
                () -> verify(memberRepository, times(0)).isPhoneExists(any()),
                () -> verify(memberRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("이미 사용하고 있는 전화번호면 회원가입에 실패한다")
    void throwExceptionByDuplicatePhone() {
        // given
        given(memberRepository.isEmailExists(any())).willReturn(false);
        given(memberRepository.isNicknameExists(any())).willReturn(false);
        given(memberRepository.isPhoneExists(any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> signUpMemberService.signUp(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).isEmailExists(any()),
                () -> verify(memberRepository, times(1)).isNicknameExists(any()),
                () -> verify(memberRepository, times(1)).isPhoneExists(any()),
                () -> verify(memberRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("모든 중복 검사를 통과한다면 회원가입에 성공한다")
    void success() {
        // given
        given(memberRepository.isEmailExists(any())).willReturn(false);
        given(memberRepository.isNicknameExists(any())).willReturn(false);
        given(memberRepository.isPhoneExists(any())).willReturn(false);

        final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
        given(memberRepository.save(any())).willReturn(member);

        // when
        final Long savedMemberId = signUpMemberService.signUp(command);

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).isEmailExists(any()),
                () -> verify(memberRepository, times(1)).isNicknameExists(any()),
                () -> verify(memberRepository, times(1)).isPhoneExists(any()),
                () -> verify(memberRepository, times(1)).save(any()),
                () -> assertThat(savedMemberId).isEqualTo(member.getId())
        );
    }
}
