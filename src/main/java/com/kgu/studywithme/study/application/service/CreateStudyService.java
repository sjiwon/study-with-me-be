package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.service.MemberReader;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.adapter.StudyDuplicateCheckRepositoryAdapter;
import com.kgu.studywithme.study.application.usecase.command.CreateStudyUseCase;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyLocation;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.study.domain.StudyType.ONLINE;

@Service
@RequiredArgsConstructor
public class CreateStudyService implements CreateStudyUseCase {
    private final MemberReader memberReader;
    private final StudyDuplicateCheckRepositoryAdapter studyDuplicateCheckRepositoryAdapter;
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;

    @Override
    public Long invoke(final Command command) {
        validateStudyNameIsUnique(command.name());

        final Member host = memberReader.getById(command.hostId());
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

    private Study buildStudy(
            final Member host,
            final Command command
    ) {
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
