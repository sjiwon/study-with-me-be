CREATE TABLE IF NOT EXISTS member
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    nickname         VARCHAR(100) NOT NULL UNIQUE,
    email            VARCHAR(150) NOT NULL UNIQUE,
    birth            DATE         NOT NULL,
    phone            VARCHAR(13)  NOT NULL UNIQUE,
    gender           VARCHAR(6)   NOT NULL,
    province         VARCHAR(100) NOT NULL,
    city             VARCHAR(100) NOT NULL,
    score            INT          NOT NULL,
    is_email_opt_in  TINYINT(1)   NOT NULL,
    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_token
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT       NOT NULL,
    refresh_token    VARCHAR(150) NOT NULL UNIQUE,
    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_interest
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT      NOT NULL,
    category  VARCHAR(20) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_report
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    reportee_id      BIGINT      NOT NULL,
    reporter_id      BIGINT      NOT NULL,
    reason           TEXT        NOT NULL,
    status           VARCHAR(10) NOT NULL,
    created_at       DATETIME    NOT NULL,
    last_modified_at DATETIME    NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_review
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    reviewee_id      BIGINT       NOT NULL,
    reviewer_id      BIGINT       NOT NULL,
    content          VARCHAR(255) NOT NULL,
    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id              BIGINT       NOT NULL,
    name                 VARCHAR(100) NOT NULL UNIQUE,
    description          TEXT         NOT NULL,
    category             VARCHAR(20)  NOT NULL,
    capacity             INT          NOT NULL,
    participants         INT          NOT NULL,
    thumbnail            VARCHAR(100) NOT NULL,
    study_type           VARCHAR(10)  NOT NULL,
    province             VARCHAR(100),
    city                 VARCHAR(100),
    recruitment_status   VARCHAR(12)  NOT NULL,
    minimum_attendance   INT          NOT NULL,
    policy_update_chance INT          NOT NULL,
    is_terminated        TINYINT(1)   NOT NULL,
    favorite_count       INT          NOT NULL DEFAULT 0,
    review_count         INT          NOT NULL DEFAULT 0,
    created_at           DATETIME     NOT NULL,
    last_modified_at     DATETIME     NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_hashtag
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id BIGINT       NOT NULL,
    name     VARCHAR(100) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_weekly
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id             BIGINT       NOT NULL,
    creator_id           BIGINT       NOT NULL,
    week                 INT          NOT NULL,
    title                VARCHAR(255) NOT NULL,
    content              TEXT         NOT NULL,
    start_date           DATETIME     NOT NULL,
    end_date             DATETIME     NOT NULL,
    is_assignment_exists TINYINT(1)   NOT NULL,
    is_auto_attendance   TINYINT(1)   NOT NULL,
    created_at           DATETIME     NOT NULL,
    last_modified_at     DATETIME     NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_weekly_attachment
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    week_id          BIGINT       NOT NULL,
    link             VARCHAR(200) NOT NULL,
    upload_file_name VARCHAR(200) NOT NULL,
    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_weekly_submit
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    week_id          BIGINT       NOT NULL,
    participant_id   BIGINT       NOT NULL,
    submit_type      VARCHAR(10)  NOT NULL,
    upload_file_name VARCHAR(200),
    link             VARCHAR(255) NOT NULL,
    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_attendance
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id         BIGINT      NOT NULL,
    week             INT         NOT NULL,
    participant_id   BIGINT      NOT NULL,
    status           VARCHAR(15) NOT NULL,
    created_at       DATETIME    NOT NULL,
    last_modified_at DATETIME    NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_notice
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id         BIGINT       NOT NULL,
    writer_id        BIGINT       NOT NULL,
    title            VARCHAR(100) NOT NULL,
    content          TEXT         NOT NULL,
    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_notice_comment
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    notice_id        BIGINT   NOT NULL,
    writer_id        BIGINT   NOT NULL,
    content          TEXT     NOT NULL,
    created_at       DATETIME NOT NULL,
    last_modified_at DATETIME NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_participant
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id         BIGINT      NOT NULL,
    member_id        BIGINT      NOT NULL,
    status           VARCHAR(10) NOT NULL,
    created_at       DATETIME    NOT NULL,
    last_modified_at DATETIME    NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_review
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id         BIGINT       NOT NULL,
    writer_id        BIGINT       NOT NULL,
    content          VARCHAR(255) NOT NULL,
    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS favorite
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT   NOT NULL,
    study_id         BIGINT   NOT NULL,
    created_at       DATETIME NOT NULL,
    last_modified_at DATETIME NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE member_token
    ADD CONSTRAINT fk_member_token_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE member_interest
    ADD CONSTRAINT fk_member_interest_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE member_report
    ADD CONSTRAINT fk_member_report_reportee_id_from_member
        FOREIGN KEY (reportee_id)
            REFERENCES member (id);

ALTER TABLE member_report
    ADD CONSTRAINT fk_member_report_reporter_id_from_member
        FOREIGN KEY (reporter_id)
            REFERENCES member (id);

ALTER TABLE member_review
    ADD CONSTRAINT fk_member_review_reviewee_id_from_member
        FOREIGN KEY (reviewee_id)
            REFERENCES member (id);

ALTER TABLE member_review
    ADD CONSTRAINT fk_member_review_reviewer_id_from_member
        FOREIGN KEY (reviewer_id)
            REFERENCES member (id);

ALTER TABLE study
    ADD CONSTRAINT fk_study_host_id_from_member
        FOREIGN KEY (host_id)
            REFERENCES member (id);

ALTER TABLE study_hashtag
    ADD CONSTRAINT fk_study_hashtag_study_id_from_study
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_weekly
    ADD CONSTRAINT fk_study_weekly_study_id_from_study
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_weekly
    ADD CONSTRAINT fk_study_weekly_creator_id_from_member
        FOREIGN KEY (creator_id)
            REFERENCES member (id);

ALTER TABLE study_weekly_attachment
    ADD CONSTRAINT fk_study_weekly_attachment_week_id_from_study_weekly
        FOREIGN KEY (week_id)
            REFERENCES study_weekly (id);

ALTER TABLE study_weekly_submit
    ADD CONSTRAINT fk_study_weekly_submit_week_id_from_study_weekly
        FOREIGN KEY (week_id)
            REFERENCES study_weekly (id);

ALTER TABLE study_weekly_submit
    ADD CONSTRAINT fk_study_weekly_submit_participant_id_from_member
        FOREIGN KEY (participant_id)
            REFERENCES member (id);

ALTER TABLE study_attendance
    ADD CONSTRAINT fk_study_attendance_study_id_from_study
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_attendance
    ADD CONSTRAINT fk_study_attendance_participant_id_from_member
        FOREIGN KEY (participant_id)
            REFERENCES member (id);

ALTER TABLE study_notice
    ADD CONSTRAINT fk_study_notice_study_id_from_study
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_notice
    ADD CONSTRAINT fk_study_notice_writer_id_from_member
        FOREIGN KEY (writer_id)
            REFERENCES member (id);

ALTER TABLE study_notice_comment
    ADD CONSTRAINT fk_study_notice_comment_notice_id_from_study_notice
        FOREIGN KEY (notice_id)
            REFERENCES study_notice (id);

ALTER TABLE study_notice_comment
    ADD CONSTRAINT fk_study_notice_comment_writer_id_from_member
        FOREIGN KEY (writer_id)
            REFERENCES member (id);

ALTER TABLE study_participant
    ADD CONSTRAINT fk_study_participant_study_id_from_study
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_participant
    ADD CONSTRAINT fk_study_participant_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE study_review
    ADD CONSTRAINT fk_study_review_study_id_from_study
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_review
    ADD CONSTRAINT fk_study_review_writer_id_from_member
        FOREIGN KEY (writer_id)
            REFERENCES member (id);

ALTER TABLE favorite
    ADD CONSTRAINT fk_favorite_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE favorite
    ADD CONSTRAINT fk_favorite_study_id_from_study
        FOREIGN KEY (study_id)
            REFERENCES study (id);

-- 스터디 찜 관련 쿼리 최적화
ALTER TABLE favorite
    ADD UNIQUE INDEX idx_favorite_member_id_study_id (member_id, study_id);

-- 사용자 정보 조회 -> 관심사 조회 쿼리 최적화
ALTER TABLE member_interest
    ADD INDEX idx_member_interest_member_id_category (member_id, category);

-- 사용자가 신청한/참여중인 스터디 조회 쿼리 최적화
ALTER TABLE study_participant
    ADD INDEX idx_study_participant_member_id_status (member_id, status);

-- 사용자 출석률 조회 쿼리 최적화
ALTER TABLE study_attendance
    ADD INDEX idx_study_attendance_participant_id_status (participant_id, status);

-- 신청자 Validation 관련 쿼리 최적화
ALTER TABLE study_participant
    ADD INDEX idx_study_participant_study_id_member_id_status (study_id, member_id, status);

-- 출석 횟수 조회 쿼리 최적화
ALTER TABLE study_attendance
    ADD INDEX idx_study_attendance_study_id_participant_id_status (study_id, participant_id, status);

-- 스터디 해시태그 조회 쿼리 최적화
ALTER TABLE study_hashtag
    ADD INDEX idx_study_hashtag_study_id_name (study_id, name);

-- 스터디 신청자 조회 쿼리 최적화
ALTER TABLE study_participant
    ADD INDEX idx_study_participant_study_id_status (study_id, status);

-- 스터디 출석 정보 조회 쿼리 최적화
ALTER TABLE study_attendance
    ADD INDEX idx_study_attendance_study_id_week (study_id, week);

-- 스터디 메인페이지 조회 쿼리 최적화
ALTER TABLE study
    ADD INDEX idx_study_category_is_terminated (category, is_terminated);
ALTER TABLE study
    ADD INDEX idx_study_category_is_terminated_favorite_count (category, is_terminated, favorite_count);
ALTER TABLE study
    ADD INDEX idx_study_category_is_terminated_review_count (category, is_terminated, review_count);
ALTER TABLE study
    ADD INDEX idx_study_study_type_category_is_terminated (study_type, category, is_terminated);
ALTER TABLE study
    ADD INDEX idx_study_study_type_category_is_terminated_favorite_count (study_type, category, is_terminated, favorite_count);
ALTER TABLE study
    ADD INDEX idx_study_study_type_category_is_terminated_review_count (study_type, category, is_terminated, review_count);

ALTER TABLE study_attendance
    ADD INDEX idx_study_attendance_status_study_id_week_participant_id (status, study_id, week, participant_id);

ALTER TABLE study_weekly
    ADD INDEX idx_study_weekly_end_date_is_auto_attendance_study_id_week (end_date, is_auto_attendance, study_id, week);

