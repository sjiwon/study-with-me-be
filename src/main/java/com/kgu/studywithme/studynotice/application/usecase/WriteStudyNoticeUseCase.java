package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class WriteStudyNoticeUseCase {
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final StudyNoticeRepository studyNoticeRepository;

    public Long invoke(final WriteStudyNoticeCommand command) {
        final Study study = studyRepository.getInProgressStudy(command.studyId());
        final Member host = memberRepository.getById(command.hostId());
        return studyNoticeRepository.save(StudyNotice.writeNotice(study, host, command.title(), command.content())).getId();
    }
}
