package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberReadAdapter;
import com.kgu.studywithme.member.domain.Member;
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
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class CreateStudyService implements CreateStudyUseCase {
    private final MemberReadAdapter memberReadAdapter;
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;

    @Override
    public Long invoke(final Command command) {
        validateStudyNameIsUnique(command.name());

        final Member host = memberReadAdapter.getById(command.hostId());
        final Study study = studyRepository.save(buildStudy(command, host));

        final StudyParticipant participant = StudyParticipant.applyHost(study.getId(), command.hostId());
        studyParticipantRepository.save(participant);
        return study.getId();
    }

    private void validateStudyNameIsUnique(final StudyName name) {
        if (studyRepository.isNameExists(name.getValue())) {
            throw StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME);
        }
    }

    private Study buildStudy(
            final Command command,
            final Member host
    ) {
        if (command.type() == ONLINE) {
            return Study.createOnlineStudy(
                    host.getId(),
                    command.name(),
                    command.description(),
                    command.capacity(),
                    command.category(),
                    command.thumbnail(),
                    command.minimumAttendanceForGraduation(),
                    command.hashtags()
            );
        }

        return Study.createOfflineStudy(
                host.getId(),
                command.name(),
                command.description(),
                command.capacity(),
                command.category(),
                command.thumbnail(),
                new StudyLocation(command.province(), command.city()),
                command.minimumAttendanceForGraduation(),
                command.hashtags()
        );
    }
}
