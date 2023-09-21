package com.kgu.studywithme.member.domain.service;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.member.domain.model.Phone;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> MemberResourceValidator 테스트")
public class MemberResourceValidatorTest extends ParallelTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final MemberResourceValidator sut = new MemberResourceValidator(memberRepository);

    @Nested
    @DisplayName("회원가입 시 리소스 검증 (이메일, 닉네임, 전화번호)")
    class ValidateInSignUp {
        private final Member member = JIWON.toMember();

        @Test
        @DisplayName("이메일이 중복되면 예외가 발생한다")
        void throwExceptionByDuplicateEmail() {
            // given
            given(memberRepository.existsByEmailValue(member.getEmail().getValue())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.validateInSignUp(member.getEmail(), member.getNickname(), member.getPhone()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).existsByEmailValue(member.getEmail().getValue()),
                    () -> verify(memberRepository, times(0)).existsByNicknameValue(member.getNickname().getValue()),
                    () -> verify(memberRepository, times(0)).existsByPhoneValue(member.getPhone().getValue())
            );
        }

        @Test
        @DisplayName("닉네임이 중복되면 예외가 발생한다")
        void throwExceptionByDuplicateNickname() {
            // given
            given(memberRepository.existsByEmailValue(member.getEmail().getValue())).willReturn(false);
            given(memberRepository.existsByNicknameValue(member.getNickname().getValue())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.validateInSignUp(member.getEmail(), member.getNickname(), member.getPhone()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).existsByEmailValue(member.getEmail().getValue()),
                    () -> verify(memberRepository, times(1)).existsByNicknameValue(member.getNickname().getValue()),
                    () -> verify(memberRepository, times(0)).existsByPhoneValue(member.getPhone().getValue())
            );
        }

        @Test
        @DisplayName("전화번호가 중복되면 예외가 발생한다")
        void throwExceptionByDuplicatePhone() {
            // given
            given(memberRepository.existsByEmailValue(member.getEmail().getValue())).willReturn(false);
            given(memberRepository.existsByNicknameValue(member.getNickname().getValue())).willReturn(false);
            given(memberRepository.existsByPhoneValue(member.getPhone().getValue())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.validateInSignUp(member.getEmail(), member.getNickname(), member.getPhone()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).existsByEmailValue(member.getEmail().getValue()),
                    () -> verify(memberRepository, times(1)).existsByNicknameValue(member.getNickname().getValue()),
                    () -> verify(memberRepository, times(1)).existsByPhoneValue(member.getPhone().getValue())
            );
        }

        @Test
        @DisplayName("검증에 성공한다")
        void success() {
            // given
            given(memberRepository.existsByEmailValue(member.getEmail().getValue())).willReturn(false);
            given(memberRepository.existsByNicknameValue(member.getNickname().getValue())).willReturn(false);
            given(memberRepository.existsByPhoneValue(member.getPhone().getValue())).willReturn(false);

            // when - then
            assertDoesNotThrow(() -> sut.validateInSignUp(member.getEmail(), member.getNickname(), member.getPhone()));

            assertAll(
                    () -> verify(memberRepository, times(1)).existsByEmailValue(member.getEmail().getValue()),
                    () -> verify(memberRepository, times(1)).existsByNicknameValue(member.getNickname().getValue()),
                    () -> verify(memberRepository, times(1)).existsByPhoneValue(member.getPhone().getValue())
            );
        }
    }

    @Nested
    @DisplayName("수정 시 리소스 검증 (이메일, 닉네임, 전화번호)")
    class ValidateInUpdate {
        private final Member member = JIWON.toMember().apply(1L);
        private final Nickname newNickname = new Nickname(member.getNickname().getValue() + "diff");
        private final Phone newPhone = new Phone(member.getPhone().getValue().replaceAll("0", "9"));

        @Test
        @DisplayName("타인이 닉네임을 사용하고 있으면 예외가 발생한다")
        void throwExceptionByDuplicateNickname() {
            // given
            given(memberRepository.isNicknameUsedByOther(member.getId(), newNickname.getValue())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.validateInUpdate(member.getId(), newNickname, newPhone))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).isNicknameUsedByOther(member.getId(), newNickname.getValue()),
                    () -> verify(memberRepository, times(0)).isPhoneUsedByOther(member.getId(), newPhone.getValue())
            );
        }

        @Test
        @DisplayName("타인이 전화번호를 사용하고 있으면 예외가 발생한다")
        void throwExceptionByDuplicatePhone() {
            // given
            given(memberRepository.isNicknameUsedByOther(member.getId(), newNickname.getValue())).willReturn(false);
            given(memberRepository.isPhoneUsedByOther(member.getId(), newPhone.getValue())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.validateInUpdate(member.getId(), newNickname, newPhone))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).isNicknameUsedByOther(member.getId(), newNickname.getValue()),
                    () -> verify(memberRepository, times(1)).isPhoneUsedByOther(member.getId(), newPhone.getValue())
            );
        }

        @Test
        @DisplayName("검증에 성공한다")
        void success() {
            // given
            given(memberRepository.isNicknameUsedByOther(member.getId(), newNickname.getValue())).willReturn(false);
            given(memberRepository.isPhoneUsedByOther(member.getId(), newPhone.getValue())).willReturn(false);

            // when - then
            assertDoesNotThrow(() -> sut.validateInUpdate(member.getId(), newNickname, newPhone));

            assertAll(
                    () -> verify(memberRepository, times(1)).isNicknameUsedByOther(member.getId(), newNickname.getValue()),
                    () -> verify(memberRepository, times(1)).isPhoneUsedByOther(member.getId(), newPhone.getValue())
            );
        }
    }
}
