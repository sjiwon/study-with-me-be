package com.kgu.studywithme.favorite.application.usecase;

import com.kgu.studywithme.common.IntegrateTest;
import com.kgu.studywithme.common.fixture.MemberFixture;
import com.kgu.studywithme.favorite.application.usecase.command.MarkStudyLikeCommand;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Favorite -> 스터디 찜 동시성 테스트")
public class MarkStudyLikeConcurrencyTest extends IntegrateTest {
    @Autowired
    private ManageFavoriteUseCase sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int TOTAL_THREAD_COUNT = 10;
    private final Member[] members = new Member[10];
    private Study study;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;
    private AtomicInteger successCount;
    private AtomicInteger failCount;

    @BeforeEach
    void setUp() {
        final List<MemberFixture> fixtures = Arrays.stream(MemberFixture.values())
                .limit(TOTAL_THREAD_COUNT)
                .toList();
        Arrays.setAll(members, (index) -> memberRepository.save(fixtures.get(index).toMember()));
        study = studyRepository.save(SPRING.toStudy(members[0]));

        executorService = Executors.newFixedThreadPool(TOTAL_THREAD_COUNT);
        countDownLatch = new CountDownLatch(TOTAL_THREAD_COUNT);
        successCount = new AtomicInteger();
        failCount = new AtomicInteger();
    }

    @Test
    @DisplayName("특정 사용자가 스터디 찜 등록을 동시에 10번 요청하면 1번만 등록이 되어야 한다")
    void sameUser() throws InterruptedException {
        // when
        for (int i = 0; i < TOTAL_THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    sut.markLike(new MarkStudyLikeCommand(members[0].getId(), study.getId()));
                    successCount.getAndIncrement();
                } catch (final Throwable e) {
                    failCount.getAndIncrement();
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        assertAll(
                () -> assertThat(successCount.get()).isEqualTo(1),
                () -> assertThat(failCount.get()).isEqualTo(9),
                () -> assertThat(getMarkedCount()).isEqualTo(1),
                () -> assertThat(studyRepository.getById(study.getId()).getFavoriteCount()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("10명의 사용자가 스터디 찜 등록을 진행하면 study's favoriteCount는 10이 되어야 한다")
    void diffUser() throws InterruptedException {
        // when
        for (int i = 0; i < TOTAL_THREAD_COUNT; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    sut.markLike(new MarkStudyLikeCommand(members[index].getId(), study.getId()));
                    successCount.getAndIncrement();
                } catch (final Throwable e) {
                    failCount.getAndIncrement();
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        assertAll(
                () -> assertThat(successCount.get()).isEqualTo(10),
                () -> assertThat(failCount.get()).isEqualTo(0),
                () -> assertThat(getMarkedCount()).isEqualTo(10),
                () -> assertThat(studyRepository.getById(study.getId()).getFavoriteCount()).isEqualTo(10)
        );
    }

    private Long getMarkedCount() {
        return em.createQuery(
                        "SELECT COUNT(f)" +
                                " FROM Favorite f" +
                                " WHERE f.study.id = :studyId",
                        Long.class
                ).setParameter("studyId", study.getId())
                .getResultList()
                .get(0);
    }
}
