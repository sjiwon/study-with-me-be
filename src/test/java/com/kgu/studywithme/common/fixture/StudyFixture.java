package com.kgu.studywithme.common.fixture;

import com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture;
import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.study.domain.model.Capacity;
import com.kgu.studywithme.study.domain.model.Description;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.model.StudyLocation;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.model.StudyThumbnail;
import com.kgu.studywithme.study.domain.model.StudyType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static com.kgu.studywithme.category.domain.model.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.model.Category.LANGUAGE;
import static com.kgu.studywithme.category.domain.model.Category.PROGRAMMING;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_INTERVIEW_001;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_INTERVIEW_002;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_INTERVIEW_003;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_INTERVIEW_004;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_INTERVIEW_005;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_LANGUAGE_001;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_LANGUAGE_002;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_LANGUAGE_003;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_LANGUAGE_004;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_LANGUAGE_005;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_PROGRAMMING_001;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_PROGRAMMING_002;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_PROGRAMMING_003;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_PROGRAMMING_004;
import static com.kgu.studywithme.study.domain.model.StudyThumbnail.IMAGE_PROGRAMMING_005;
import static com.kgu.studywithme.study.domain.model.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.model.StudyType.ONLINE;

@Getter
@RequiredArgsConstructor
public enum StudyFixture {
    TOEIC(
            new StudyName("TOEIC 스터디"), new Description("TOEIC 스터디입니다"), LANGUAGE,
            IMAGE_LANGUAGE_001, ONLINE,
            null, new Capacity(8), 0,
            new HashSet<>(Set.of("언어", "토익", "TOEIC"))
    ),
    TOEFL(
            new StudyName("TOEFL 스터디"), new Description("TOEFL 스터디입니다"), LANGUAGE,
            IMAGE_LANGUAGE_002, ONLINE,
            null, new Capacity(4), 6,
            new HashSet<>(Set.of("언어", "토플", "TOEFL"))
    ),
    JAPANESE(
            new StudyName("일본어 스터디"), new Description("일본어 스터디입니다"), LANGUAGE,
            IMAGE_LANGUAGE_005, ONLINE,
            null, new Capacity(5), 7,
            new HashSet<>(Set.of("언어", "일본어"))
    ),
    CHINESE(
            new StudyName("중국어 스터디"), new Description("중국어 스터디입니다"), LANGUAGE,
            IMAGE_LANGUAGE_003, ONLINE,
            null, new Capacity(5), 8,
            new HashSet<>(Set.of("언어", "중국어"))
    ),
    FRENCH(
            new StudyName("프랑스어 스터디"), new Description("프랑스어 스터디입니다"), LANGUAGE,
            IMAGE_LANGUAGE_004, ONLINE,
            null, new Capacity(6), 9,
            new HashSet<>(Set.of("언어", "프랑스어"))
    ),
    GERMAN(
            new StudyName("독일어 스터디"), new Description("독일어 스터디입니다"), LANGUAGE,
            IMAGE_LANGUAGE_002, ONLINE,
            null, new Capacity(8), 10,
            new HashSet<>(Set.of("언어", "독일어"))
    ),
    ARABIC(
            new StudyName("아랍어 스터디"), new Description("아랍어 스터디입니다"), LANGUAGE,
            IMAGE_LANGUAGE_001, ONLINE,
            null, new Capacity(5), 11,
            new HashSet<>(Set.of("언어", "아랍어"))
    ),

    TOSS_INTERVIEW(
            new StudyName("Toss 면접 스터디"), new Description("Toss 기술 면접을 대비하기 위한 스터디입니다"), INTERVIEW,
            IMAGE_INTERVIEW_001, OFFLINE,
            new StudyLocation("서울특별시", "강남구"), new Capacity(10), 15,
            new HashSet<>(Set.of("면접", "토스", "기술 면접"))
    ),
    KAKAO_INTERVIEW(
            new StudyName("Kakao 면접 스터디"), new Description("Kakao 기술 면접을 대비하기 위한 스터디입니다"), INTERVIEW,
            IMAGE_INTERVIEW_002, OFFLINE,
            new StudyLocation("경기도", "성남시"), new Capacity(10), 17,
            new HashSet<>(Set.of("면접", "카카오", "기술 면접"))
    ),
    NAVER_INTERVIEW(
            new StudyName("Naver 면접 스터디"), new Description("Naver 기술 면접을 대비하기 위한 스터디입니다"), INTERVIEW,
            IMAGE_INTERVIEW_003, OFFLINE,
            new StudyLocation("경기도", "성남시"), new Capacity(10), 20,
            new HashSet<>(Set.of("면접", "네이버", "기술 면접"))
    ),
    LINE_INTERVIEW(
            new StudyName("LINE 면접 스터디"), new Description("LINE 기술 면접을 대비하기 위한 스터디입니다"), INTERVIEW,
            IMAGE_INTERVIEW_004, OFFLINE,
            new StudyLocation("경기도", "성남시"), new Capacity(10), 0,
            new HashSet<>(Set.of("면접", "라인", "기술 면접"))
    ),
    GOOGLE_INTERVIEW(
            new StudyName("Google 면접 스터디"), new Description("Google 기술 면접을 대비하기 위한 스터디입니다"), INTERVIEW,
            IMAGE_INTERVIEW_005, OFFLINE,
            new StudyLocation("서울특별시", "강남구"), new Capacity(10), 10,
            new HashSet<>(Set.of("면접", "구글", "기술 면접"))
    ),

    SPRING(
            new StudyName("Spring 스터디"), new Description("Spring 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_001, ONLINE,
            null, new Capacity(10), 10,
            new HashSet<>(Set.of("프로그래밍", "스프링", "Spring", "김영한"))
    ),
    JPA(
            new StudyName("JPA 스터디"), new Description("JPA 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_002, ONLINE,
            null, new Capacity(4), 12,
            new HashSet<>(Set.of("프로그래밍", "JPA", "Hibernate", "김영한"))
    ),
    REAL_MYSQL(
            new StudyName("Real MySQL 스터디"), new Description("Real MySQL 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_003, OFFLINE,
            new StudyLocation("서울특별시", "강남구"), new Capacity(10), 14,
            new HashSet<>(Set.of("DB", "Real MySQL", "DBA"))
    ),
    KOTLIN(
            new StudyName("코틀린 스터디"), new Description("코틀린 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_004, ONLINE,
            null, new Capacity(10), 16,
            new HashSet<>(Set.of("프로그래밍", "코틀린", "Kotlin"))
    ),
    NETWORK(
            new StudyName("네트워크 스터디"), new Description("네트워크 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_005, ONLINE,
            null, new Capacity(7), 18,
            new HashSet<>(Set.of("네트워크", "인프라", "OSI 7 Layer", "TCP/IP"))
    ),
    EFFECTIVE_JAVA(
            new StudyName("이펙티브 자바 스터디"), new Description("이펙티브 자바 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_004, ONLINE,
            null, new Capacity(8), 20,
            new HashSet<>(Set.of("프로그래밍", "자바", "이펙티브 자바"))
    ),
    AWS(
            new StudyName("AWS 스터디"), new Description("AWS 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_004, OFFLINE,
            new StudyLocation("서울특별시", "강남구"), new Capacity(10), 18,
            new HashSet<>(Set.of("AWS", "클라우드 플랫폼", "배포"))
    ),
    DOCKER(
            new StudyName("Docker 스터디"), new Description("Docker 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_001, ONLINE,
            null, new Capacity(6), 16,
            new HashSet<>(Set.of("Docker", "컨테이너"))
    ),
    KUBERNETES(
            new StudyName("Kubernetes 스터디"), new Description("Kubernetes 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_001, ONLINE,
            null, new Capacity(10), 14,
            new HashSet<>(Set.of("Kubernetes", "인프라"))
    ),
    PYTHON(
            new StudyName("파이썬 스터디"), new Description("파이썬 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_002, ONLINE,
            null, new Capacity(10), 12,
            new HashSet<>(Set.of("프로그래밍", "파이썬", "Python", "Flask"))
    ),
    RUST(
            new StudyName("러스트 스터디"), new Description("러스트 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_001, ONLINE,
            null, new Capacity(6), 10,
            new HashSet<>(Set.of("프로그래밍", "러스트", "Rust"))
    ),
    OS(
            new StudyName("운영체제 스터디"), new Description("운영체제 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_003, ONLINE,
            null, new Capacity(6), 8,
            new HashSet<>(Set.of("OS", "운영체제", "프로세스와 쓰레드", "데드락"))
    ),
    KAFKA( // 참여 인원 2 & 졸업 최소 조건 0
            new StudyName("Kafka 스터디"), new Description("Kafka 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_003, ONLINE,
            null, new Capacity(2), 0,
            new HashSet<>(Set.of("카프카", "메시지 큐", "Event Driven"))
    ),
    RABBITMQ( // 참여 인원 10 & 졸업 최소 조건 0
            new StudyName("RabbitMQ 스터디"), new Description("RabbitMQ 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_003, ONLINE,
            null, new Capacity(10), 0,
            new HashSet<>(Set.of("RabbitMQ", "메시지 큐"))
    ),
    OOP(
            new StudyName("객체지향 스터디"), new Description("객사오 읽는 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_003, ONLINE,
            null, new Capacity(10), 0,
            new HashSet<>(Set.of("객체지향 사실과 오해", "자바", "OOP"))
    ),
    REDIS(
            new StudyName("Redis 스터디"), new Description("레디스 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_003, ONLINE,
            null, new Capacity(10), 0,
            new HashSet<>(Set.of("레디스", "인메모리"))
    ),
    JSP(
            new StudyName("JSP 스터디"), new Description("JSP 스터디입니다"), PROGRAMMING,
            IMAGE_PROGRAMMING_003, ONLINE,
            null, new Capacity(10), 0,
            new HashSet<>(Set.of("JSP", "자바"))
    ),
    ;

    private final StudyName name;
    private final Description description;
    private final Category category;
    private final StudyThumbnail thumbnail;
    private final StudyType type;
    private final StudyLocation location;
    private final Capacity capacity;
    private final int minimumAttendanceForGraduation;
    private final Set<String> hashtags;

    public Study toOnlineStudy(final Long hostId) {
        return Study.createOnlineStudy(
                hostId,
                name,
                description,
                category,
                capacity,
                thumbnail,
                minimumAttendanceForGraduation,
                hashtags
        );
    }

    public Study toOfflineStudy(final Long hostId) {
        return Study.createOfflineStudy(
                hostId,
                name,
                description,
                category,
                capacity,
                thumbnail,
                location,
                minimumAttendanceForGraduation,
                hashtags
        );
    }

    public Long 스터디를_생성한다(final String accessToken) {
        return StudyAcceptanceFixture.스터디를_생성한다(accessToken, this)
                .extract()
                .jsonPath()
                .getLong("studyId");
    }
}
