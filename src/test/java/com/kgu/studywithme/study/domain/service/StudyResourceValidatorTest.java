package com.kgu.studywithme.study.domain.service;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Study -> StudyResourceValidator 테스트")
public class StudyResourceValidatorTest extends ParallelTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final StudyResourceValidator sut = new StudyResourceValidator(studyRepository);

    private final Member host = JIWON.toMember().apply(1L);

    @Nested
    @DisplayName("스터디 생성 시 리소스 검증 (이름)")
    class ValidateInCreate {
        private final Study study = SPRING.toStudy(host);

        @Test
        @DisplayName("이름이 중복되면 예외가 발생한다")
        void throwExceptionByDuplicateName() {
            // given
            given(studyRepository.existsByNameValue(study.getName().getValue())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.validateInCreate(study.getName()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());

            verify(studyRepository, times(1)).existsByNameValue(study.getName().getValue());
        }

        @Test
        @DisplayName("검증에 성공한다")
        void success() {
            // given
            given(studyRepository.existsByNameValue(study.getName().getValue())).willReturn(false);

            // when - then
            assertDoesNotThrow(() -> sut.validateInCreate(study.getName()));

            verify(studyRepository, times(1)).existsByNameValue(study.getName().getValue());
        }
    }

    @Nested
    @DisplayName("수정 시 리소스 검증 (이름)")
    class ValidateInUpdate {
        private final Study study = SPRING.toStudy(host).apply(1L);
        private final StudyName newName = new StudyName(study.getName().getValue() + "diff");

        @Test
        @DisplayName("다른 스터디가 해당 이름을 사용하고 있으면 예외가 발생한다")
        void throwExceptionByDuplicateName() {
            // given
            given(studyRepository.isNameUsedByOther(study.getId(), newName.getValue())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.validateInUpdate(study.getId(), newName))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.DUPLICATE_NAME.getMessage());

            verify(studyRepository, times(1)).isNameUsedByOther(study.getId(), newName.getValue());
        }

        @Test
        @DisplayName("검증에 성공한다")
        void success1() {
            // given
            given(studyRepository.isNameUsedByOther(study.getId(), newName.getValue())).willReturn(false);

            // when - then
            assertDoesNotThrow(() -> sut.validateInUpdate(study.getId(), newName));

            verify(studyRepository, times(1)).isNameUsedByOther(study.getId(), newName.getValue());
        }
    }
}
