package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.application.usecase.command.CreateStudyCommand;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.model.StudyLocation;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.domain.service.StudyResourceValidator;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.study.domain.model.StudyType.ONLINE;

@Service
@RequiredArgsConstructor
public class CreateStudyUseCase {
    private final StudyResourceValidator studyResourceValidator;
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;

    public Long invoke(final CreateStudyCommand command) {
        studyResourceValidator.validateInCreate(command.name());

        final Member host = memberRepository.getById(command.hostId());
        final Study study = studyRepository.save(createStudy(host, command));
        applyHostToParticipant(study, host);

        return study.getId();
    }

    private Study createStudy(final Member host, final CreateStudyCommand command) {
        if (command.type() == ONLINE) {
            return Study.createOnlineStudy(
                    host.getId(),
                    command.name(),
                    command.description(),
                    command.category(),
                    command.capacity(),
                    command.thumbnail(),
                    command.minimumAttendanceForGraduation(),
                    command.hashtags()
            );
        }

        return Study.createOfflineStudy(
                host.getId(),
                command.name(),
                command.description(),
                command.category(),
                command.capacity(),
                command.thumbnail(),
                new StudyLocation(command.province(), command.city()),
                command.minimumAttendanceForGraduation(),
                command.hashtags()
        );
    }

    private void applyHostToParticipant(final Study study, final Member host) {
        final StudyParticipant participant = StudyParticipant.applyHost(study.getId(), host.getId());
        studyParticipantRepository.save(participant);
    }
}
