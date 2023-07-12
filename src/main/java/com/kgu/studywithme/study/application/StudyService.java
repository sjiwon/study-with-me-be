package com.kgu.studywithme.study.application;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.member.application.MemberFindService;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.*;
import com.kgu.studywithme.study.domain.participant.Capacity;
import com.kgu.studywithme.study.presentation.dto.request.StudyRegisterRequest;
import com.kgu.studywithme.study.presentation.dto.request.StudyUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.study.domain.RecruitmentStatus.COMPLETE;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;
import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyValidator studyValidator;
    private final StudyRepository studyRepository;
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;

    @StudyWithMeWritableTransactional
    public Long register(
            final Long hostId,
            final StudyRegisterRequest request
    ) {
        validateUniqueNameForCreate(request.name());

        final Member host = memberFindService.findById(hostId);
        final Study study = buildStudy(request, host);

        return studyRepository.save(study).getId();
    }

    private void validateUniqueNameForCreate(final String name) {
        studyValidator.validateUniqueNameForCreate(StudyName.from(name));
    }

    private Study buildStudy(
            final StudyRegisterRequest request,
            final Member host
    ) {
        if (request.type().equals(ONLINE.getBrief())) {
            return Study.createOnlineStudy(
                    host,
                    StudyName.from(request.name()),
                    Description.from(request.description()),
                    Capacity.from(request.capacity()),
                    Category.from(request.category()),
                    StudyThumbnail.from(request.thumbnail()),
                    request.minimumAttendanceForGraduation(),
                    request.hashtags()
            );
        } else {
            return Study.createOfflineStudy(
                    host,
                    StudyName.from(request.name()),
                    Description.from(request.description()),
                    Capacity.from(request.capacity()),
                    Category.from(request.category()),
                    StudyThumbnail.from(request.thumbnail()),
                    StudyLocation.of(request.province(), request.city()),
                    request.minimumAttendanceForGraduation(),
                    request.hashtags()
            );
        }
    }

    @StudyWithMeWritableTransactional
    public void update(
            final Long studyId,
            final Long hostId,
            final StudyUpdateRequest request
    ) {
        validateUniqueNameForUpdate(request.name(), studyId);

        final Study study = studyFindService.findByIdAndHostId(studyId, hostId);
        study.update(
                StudyName.from(request.name()),
                Description.from(request.description()),
                request.capacity(),
                Category.from(request.category()),
                StudyThumbnail.from(request.thumbnail()),
                request.type().equals(ONLINE.getBrief()) ? ONLINE : OFFLINE,
                request.province(),
                request.city(),
                request.recruitmentStatus() ? IN_PROGRESS : COMPLETE,
                request.minimumAttendanceForGraduation(),
                request.hashtags()
        );
    }

    private void validateUniqueNameForUpdate(
            final String name,
            final Long studyId
    ) {
        studyValidator.validateUniqueNameForUpdate(StudyName.from(name), studyId);
    }

    @StudyWithMeWritableTransactional
    public void close(final Long studyId) {
        final Study study = studyFindService.findById(studyId);
        study.close();
    }
}