package com.kgu.studywithme.member.domain.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Email;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.member.domain.model.Phone;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberResourceValidator {
    private final MemberRepository memberRepository;

    public void validateInSignUp(final Email email, final Nickname nickname, final Phone phone) {
        validateEmailIsUnique(email);
        validateNicknameIsUnique(nickname);
        validatePhoneIsUnique(phone);
    }

    public void validateInUpdate(final Member member) {
        validateNicknameIsInUseByOther(member.getId(), member.getNickname());
        validatePhoneIsInUseByOther(member.getId(), member.getPhone());
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
        final List<Long> ids = memberRepository.findIdByNicknameUsed(nickname.getValue());

        if (usedByOther(ids, memberId)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validatePhoneIsInUseByOther(final Long memberId, final Phone phone) {
        final List<Long> ids = memberRepository.findIdByPhoneUsed(phone.getValue());

        if (usedByOther(ids, memberId)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }

    private boolean usedByOther(final List<Long> resourceUsedIds, final Long memberId) {
        if (resourceUsedIds.isEmpty()) {
            return false;
        }

        return resourceUsedIds.size() != 1 || !resourceUsedIds.contains(memberId);
    }
}
