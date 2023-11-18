package com.kgu.studywithme.favorite.application.usecase;

import com.kgu.studywithme.common.config.DatabaseCleanerEachCallbackExtension;
import com.kgu.studywithme.common.config.MySqlTestContainersExtension;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith({
        DatabaseCleanerEachCallbackExtension.class,
        MySqlTestContainersExtension.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Favorite -> 스터디 찜 동시성 테스트")
public class MarkStudyLikeConcurrencyTest {
    @Autowired
    private ManageFavoriteUseCase sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @PersistenceContext
    private EntityManager em;

    private Member member;
    private Study study;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toStudy(member.getId()));
    }

    @Test
    @DisplayName("특정 사용자가 스터디 찜 등록을 동시에 10번 요청하면 1번만 등록이 되어야 한다")
    void execute() throws InterruptedException {
        // given
        final int threadCount = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    sut.markLike(new MarkStudyLikeCommand(member.getId(), study.getId()));
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        assertThat(getMarkedCount()).isEqualTo(1);
    }

    private Long getMarkedCount() {
        return em.createQuery(
                        "SELECT COUNT(f)" +
                                " FROM Favorite f" +
                                " WHERE f.member.id = :memberId AND f.study.id = :studyId",
                        Long.class
                ).setParameter("memberId", member.getId())
                .setParameter("studyId", study.getId())
                .getResultList()
                .get(0);
    }
}
