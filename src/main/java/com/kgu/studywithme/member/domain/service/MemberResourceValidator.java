package com.kgu.studywithme.member.domain.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Email;
import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.member.domain.model.Phone;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberResourceValidator {
    private final MemberRepository memberRepository;

    public void validateInSignUp(final Email email, final Nickname nickname, final Phone phone) {
        validateEmailIsUnique(email);
        validateNicknameIsUnique(nickname);
        validatePhoneIsUnique(phone);
    }

    public void validateInUpdate(final Long memberId, final Nickname nickname, final Phone phone) {
        validateNicknameIsInUseByOther(memberId, nickname);
        validatePhoneIsInUseByOther(memberId, phone);
    }

    private void validateEmailIsUnique(final Email email) {
        if (memberRepository.existsByEmailValue(email.getValue())) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateNicknameIsUnique(final Nickname nickname) {
        if (memberRepository.existsByNicknameValue(nickname.getValue())) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validatePhoneIsUnique(final Phone phone) {
        if (memberRepository.existsByPhoneValue(phone.getValue())) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }

    private void validateNicknameIsInUseByOther(final Long memberId, final Nickname nickname) {
        final Long nicknameUsedId = memberRepository.findIdByNicknameUsed(nickname.getValue());

        if (usedByOther(nicknameUsedId, memberId)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validatePhoneIsInUseByOther(final Long memberId, final Phone phone) {
        final Long phoneUsedId = memberRepository.findIdByPhoneUsed(phone.getValue());

        if (usedByOther(phoneUsedId, memberId)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }

    private boolean usedByOther(final Long resourceUsedId, final Long memberId) {
        return resourceUsedId != null && !resourceUsedId.equals(memberId);
    }
}
