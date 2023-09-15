package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.application.adapter.StudyDuplicateCheckRepositoryAdapter;
import com.kgu.studywithme.study.application.usecase.command.CreateStudyUseCase;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.model.StudyLocation;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.study.domain.model.StudyType.ONLINE;

@Service
@RequiredArgsConstructor
public class CreateStudyService implements CreateStudyUseCase {
    private final MemberRepository memberRepository;
    private final StudyDuplicateCheckRepositoryAdapter studyDuplicateCheckRepositoryAdapter;
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;

    @Override
    public Long invoke(final Command command) {
        validateStudyNameIsUnique(command.name());

        final Member host = memberRepository.getById(command.hostId());
        final Study study = createStudyAndApplyHostToParticipant(host, command);

        return study.getId();
    }

    private void validateStudyNameIsUnique(final StudyName name) {
        if (studyDuplicateCheckRepositoryAdapter.isNameExists(name.getValue())) {
            throw StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME);
        }
    }

    private Study createStudyAndApplyHostToParticipant(final Member host, final Command command) {
        final Study study = studyRepository.save(buildStudy(host, command));
        applyHostToParticipant(study, host);
        return study;
    }

    private Study buildStudy(final Member host, final Command command) {
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
