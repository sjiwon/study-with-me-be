package com.kgu.studywithme.dummydata;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.stub.StubEmailSender;
import com.kgu.studywithme.common.stub.StubFileUploader;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.mail.application.adapter.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kgu.studywithme.study.domain.StudyThumbnail.IMAGE_PROGRAMMING_001;
import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.LEAVE;

@Slf4j
@ActiveProfiles("dummy")
@SpringBootTest
@DisplayName("DB Query 성능 개선 테스트를 위한 더미 데이터 생성기")
public class DummyDataGenerator {
    @TestConfiguration
    static class DummyDataGeneratorConfiguration {
        @Profile("dummy")
        @Bean
        public EmailSender emailSender() {
            return new StubEmailSender();
        }

        @Profile("dummy")
        @Bean
        public FileUploader fileUploader() {
            return new StubFileUploader();
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int MEMBER_SIZE = 1_000_000;
    private static final int STUDY_SIZE = 1_000_000;

//    @Test
    @DisplayName("더미 데이터 BatchInsert")
    void batchInsert() {
        insertMember(); // 100만건
        insertMemberInterest();
        insertMemberReview();
        insertStudy(); // 100만건
        insertStudyHashtag();
        insertStudyParticipant(); // 500만건 + a
        insertStudyNotice(); // 100만건
        insertStudyNoticeComment(); // 100만건
        insertStudyWeekly(); // 100만건
        insertStudyWeeklyAttachment();
        insertStudyWeeklySubmit();
        insertStudyAttendance();
        insertStudyReview();
        insertStudyFavorite();
    }

    private void insertMember() {
        log.info("=== member 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO member (name, nickname, email, birth, phone, gender, province, city, score, is_email_opt_in)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                        """;
        final List<DummyMember> dummyMembers = createDummyMembers();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyMember dummyMember = dummyMembers.get(i);
                        ps.setString(1, dummyMember.name());
                        ps.setString(2, dummyMember.nickname());
                        ps.setString(3, dummyMember.email());
                        ps.setDate(4, dummyMember.birth());
                        ps.setString(5, dummyMember.phone());
                        ps.setString(6, dummyMember.gender());
                        ps.setString(7, dummyMember.province());
                        ps.setString(8, dummyMember.city());
                        ps.setInt(9, dummyMember.score());
                        ps.setInt(10, dummyMember.isEmailOptIn());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyMembers.size();
                    }
                }
        );

        log.info("=== member 더미 데이터 Insert 완료 ===");
    }

    private List<DummyMember> createDummyMembers() {
        final List<DummyMember> list = new ArrayList<>();
        for (int i = 1; i <= MEMBER_SIZE; i++) {
            list.add(new DummyMember(i));
        }
        return list;
    }

    private void insertMemberInterest() {
        log.info("=== member_interest 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO member_interest (member_id, category)
                        VALUES (?, ?);
                        """;
        final List<DummyMemberInterest> dummyMemberInterests = createDummyMemberInterests();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyMemberInterest dummyMemberInterest = dummyMemberInterests.get(i);
                        ps.setLong(1, dummyMemberInterest.memberId());
                        ps.setString(2, dummyMemberInterest.interest());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyMemberInterests.size();
                    }
                }
        );

        log.info("=== member_interest 더미 데이터 Insert 완료 ===");
    }

    private List<DummyMemberInterest> createDummyMemberInterests() {
        final List<DummyMemberInterest> list = new ArrayList<>();
        for (int memberId = 1; memberId <= MEMBER_SIZE; memberId++) {
            final int count = (int) (Math.random() * 4 + 1);
            for (int i = 0; i < count; i++) {
                final String category = Category.from((long) (Math.random() * 6 + 1)).name();
                list.add(new DummyMemberInterest(memberId, category));
            }
        }
        return list;
    }

    private void insertMemberReview() {
        log.info("=== member_review 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO member_review (reviewee_id, reviewer_id, content)
                        VALUES (?, ?, ?);
                        """;
        final List<DummyMemberReview> dummyMemberReviews = createDummyMemberReviews();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyMemberReview dummyMemberReview = dummyMemberReviews.get(i);
                        ps.setLong(1, dummyMemberReview.revieweeId());
                        ps.setLong(2, dummyMemberReview.reviewerId());
                        ps.setString(3, dummyMemberReview.content());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyMemberReviews.size();
                    }
                }
        );

        log.info("=== member_review 더미 데이터 Insert 완료 ===");
    }

    private List<DummyMemberReview> createDummyMemberReviews() {
        final List<DummyMemberReview> list = new ArrayList<>();
        for (int revieweeId = 1; revieweeId <= MEMBER_SIZE; revieweeId++) {
            final int count = (int) (Math.random() * 3);
            for (int j = 0; j < count; j++) {
                final int reviewerId = (int) (Math.random() * MEMBER_SIZE + 1);
                list.add(new DummyMemberReview(revieweeId, reviewerId, "Good!! - " + reviewerId));
            }
        }
        return list;
    }

    private void insertStudy() {
        log.info("=== study 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO study (host_id, name, description, category, capacity, participants, thumbnail, study_type,
                               province, city, recruitment_status, minimum_attendance, policy_update_chance,
                               is_terminated)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                        """;
        final List<DummyStudy> dummyStudies = createDummyStudies();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudy dummyStudy = dummyStudies.get(i);
                        ps.setLong(1, dummyStudy.hostId());
                        ps.setString(2, dummyStudy.name());
                        ps.setString(3, dummyStudy.description());
                        ps.setString(4, dummyStudy.category());
                        ps.setInt(5, dummyStudy.capacity());
                        ps.setInt(6, dummyStudy.participants());
                        ps.setString(7, dummyStudy.thumbnail());
                        ps.setString(8, dummyStudy.studyType());
                        ps.setString(9, dummyStudy.province());
                        ps.setString(10, dummyStudy.city());
                        ps.setString(11, dummyStudy.recruitmentStatus());
                        ps.setInt(12, dummyStudy.minimumAttendance());
                        ps.setInt(13, dummyStudy.policyUpdateChance());
                        ps.setInt(14, dummyStudy.isTerminated());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudies.size();
                    }
                }
        );

        log.info("=== study 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudy> createDummyStudies() {
        final List<DummyStudy> list = new ArrayList<>();
        for (int studyId = 1; studyId <= STUDY_SIZE / 2; studyId++) {
            final int hostId = studyId;
            final String category = Category.from((long) (Math.random() * 6 + 1)).name();
            final String thumbnail = IMAGE_PROGRAMMING_001.name();
            final String studyType = ONLINE.name();
            list.add(new DummyStudy(studyId, hostId, category, thumbnail, studyType));
        }
        for (int studyId = STUDY_SIZE / 2 + 1; studyId <= STUDY_SIZE; studyId++) {
            final int hostId = studyId;
            final String category = Category.from((long) (Math.random() * 6 + 1)).name();
            final String thumbnail = IMAGE_PROGRAMMING_001.name();
            final String studyType = OFFLINE.name();
            list.add(new DummyStudy(studyId, hostId, category, thumbnail, studyType));
        }
        return list;
    }

    private void insertStudyHashtag() {
        log.info("=== study_hashtag 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO study_hashtag (study_id, name)
                        VALUES (?, ?);
                        """;
        final List<DummyStudyHashtag> dummyStudyHashtags = createDummyStudyHashtags();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudyHashtag dummyStudyHashtag = dummyStudyHashtags.get(i);
                        ps.setLong(1, dummyStudyHashtag.studyId());
                        ps.setString(2, dummyStudyHashtag.hashtag());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudyHashtags.size();
                    }
                }
        );

        log.info("=== study_hashtag 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudyHashtag> createDummyStudyHashtags() {
        final List<String> hashtags = List.of("A", "B", "C", "D", "E");
        final List<DummyStudyHashtag> list = new ArrayList<>();
        for (int studyId = 1; studyId <= STUDY_SIZE; studyId++) {
            final Set<String> studyHashtags = new HashSet<>();

            final int count = (int) (Math.random() * 5 + 1);
            for (int j = 0; j < count; j++) {
                studyHashtags.add(hashtags.get((int) (Math.random() * 5)));
            }

            for (String studyHashtag : studyHashtags) {
                list.add(new DummyStudyHashtag(studyId, studyHashtag));
            }
        }
        return list;
    }

    private void insertStudyNotice() {
        log.info("=== study_notice 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO study_notice (study_id, writer_id, title, content)
                        VALUES (?, ?, ?, ?);
                        """;
        final List<DummyStudyNotice> dummyStudyNotices = createDummyStudyNotices();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudyNotice dummyStudyNotice = dummyStudyNotices.get(i);
                        ps.setLong(1, dummyStudyNotice.studyId());
                        ps.setLong(2, dummyStudyNotice.writerId());
                        ps.setString(3, dummyStudyNotice.title());
                        ps.setString(4, dummyStudyNotice.content());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudyNotices.size();
                    }
                }
        );

        log.info("=== study_notice 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudyNotice> createDummyStudyNotices() {
        final List<DummyStudyNotice> list = new ArrayList<>();
        for (int studyId = 1; studyId <= STUDY_SIZE; studyId++) {
            list.add(new DummyStudyNotice(studyId, studyId));
        }
        return list;
    }

    private void insertStudyNoticeComment() {
        log.info("=== study_notice_comment 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO study_notice_comment (notice_id, writer_id, content)
                        VALUES (?, ?, ?);
                        """;
        final List<DummyStudyNoticeComment> dummyStudyNoticeComments = createDummyStudyNoticeComments();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudyNoticeComment dummyStudyNoticeComment = dummyStudyNoticeComments.get(i);
                        ps.setLong(1, dummyStudyNoticeComment.noticeId());
                        ps.setLong(2, dummyStudyNoticeComment.writerId());
                        ps.setString(3, dummyStudyNoticeComment.content());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudyNoticeComments.size();
                    }
                }
        );

        log.info("=== study_notice_comment 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudyNoticeComment> createDummyStudyNoticeComments() {
        final List<DummyStudyNoticeComment> list = new ArrayList<>();
        for (int studyId = 1; studyId <= STUDY_SIZE; studyId++) {
            list.add(new DummyStudyNoticeComment(studyId, studyId));
        }
        return list;
    }

    private void insertStudyParticipant() {
        log.info("=== study_participant 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO study_participant (study_id, member_id, status)
                        VALUES (?, ?, ?);
                        """;
        final List<DummyStudyParticipant> dummyStudyParticipants = createDummyStudyParticipants();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudyParticipant dummyStudyParticipant = dummyStudyParticipants.get(i);
                        ps.setLong(1, dummyStudyParticipant.studyId());
                        ps.setLong(2, dummyStudyParticipant.memberId());
                        ps.setString(3, dummyStudyParticipant.status());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudyParticipants.size();
                    }
                }
        );

        log.info("=== study_participant 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudyParticipant> createDummyStudyParticipants() {
        final List<DummyStudyParticipant> list = new ArrayList<>();
        for (int studyId = 1; studyId <= STUDY_SIZE; studyId++) {
            list.add(new DummyStudyParticipant(studyId, studyId, APPROVE.name()));
            list.add(new DummyStudyParticipant(studyId, (studyId + 1) % MEMBER_SIZE, APPROVE.name()));
            list.add(new DummyStudyParticipant(studyId, (studyId + 2) % MEMBER_SIZE, APPROVE.name()));
            list.add(new DummyStudyParticipant(studyId, (studyId + 3) % MEMBER_SIZE, APPROVE.name()));
            list.add(new DummyStudyParticipant(studyId, (studyId + 4) % MEMBER_SIZE, APPROVE.name()));

            list.add(new DummyStudyParticipant(studyId, (int) (Math.random() * MEMBER_SIZE + 1), APPLY.name()));
            list.add(new DummyStudyParticipant(studyId, (int) (Math.random() * MEMBER_SIZE + 1), APPLY.name()));

            list.add(new DummyStudyParticipant(studyId, (int) (Math.random() * MEMBER_SIZE + 1), LEAVE.name()));
            list.add(new DummyStudyParticipant(studyId, (int) (Math.random() * MEMBER_SIZE + 1), GRADUATED.name()));
        }
        return list;
    }

    private void insertStudyReview() {
        log.info("=== study_review 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO study_review (study_id, writer_id, content)
                        VALUES (?, ?, ?);
                        """;
        final List<DummyStudyReview> dummyStudyReviews = createDummyStudyReviews();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudyReview dummyStudyReview = dummyStudyReviews.get(i);
                        ps.setLong(1, dummyStudyReview.studyId());
                        ps.setLong(2, dummyStudyReview.writerId());
                        ps.setString(3, dummyStudyReview.content());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudyReviews.size();
                    }
                }
        );

        log.info("=== study_review 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudyReview> createDummyStudyReviews() {
        final List<DummyStudyReview> list = new ArrayList<>();
        for (int studyId = 1; studyId <= 100_0000; studyId++) {
            final int count = (int) (Math.random() * 3);
            for (int j = 0; j < count; j++) {
                final int writerId = (int) (Math.random() * MEMBER_SIZE + 1);
                list.add(new DummyStudyReview(studyId, writerId));
            }
        }
        return list;
    }

    private void insertStudyWeekly() {
        log.info("=== study_weekly 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO study_weekly (study_id, creator_id, week, title, content, start_date, end_date,
                                      is_assignment_exists, is_auto_attendance)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                        """;
        final List<DummyStudyWeekly> dummyStudyWeeklies = createDummyStudyWeeklys();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudyWeekly dummyStudyWeekly = dummyStudyWeeklies.get(i);
                        ps.setLong(1, dummyStudyWeekly.studyId());
                        ps.setLong(2, dummyStudyWeekly.creatorid());
                        ps.setInt(3, dummyStudyWeekly.week());
                        ps.setString(4, dummyStudyWeekly.title());
                        ps.setString(5, dummyStudyWeekly.content());
                        ps.setTimestamp(6, dummyStudyWeekly.startDate());
                        ps.setTimestamp(7, dummyStudyWeekly.endDate());
                        ps.setInt(8, dummyStudyWeekly.isAssignmentExists());
                        ps.setInt(9, dummyStudyWeekly.isAutoAttendance());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudyWeeklies.size();
                    }
                }
        );

        log.info("=== study_weekly 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudyWeekly> createDummyStudyWeeklys() {
        final List<DummyStudyWeekly> list = new ArrayList<>();
        for (int studyId = 1; studyId <= STUDY_SIZE; studyId++) {
            list.add(new DummyStudyWeekly(studyId, studyId));
        }
        return list;
    }

    private void insertStudyWeeklyAttachment() {
        log.info("=== study_weekly_attachment 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO study_weekly_attachment (week_id, link, upload_file_name)
                        VALUES (?, ?, ?);
                        """;
        final List<DummyStudyWeeklyAttachment> dummyStudyWeeklyAttachments = createDummyStudyWeeklyAttachments();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudyWeeklyAttachment dummyStudyWeeklyAttachment = dummyStudyWeeklyAttachments.get(i);
                        ps.setLong(1, dummyStudyWeeklyAttachment.weekId());
                        ps.setString(2, dummyStudyWeeklyAttachment.link());
                        ps.setString(3, dummyStudyWeeklyAttachment.uploadFileName());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudyWeeklyAttachments.size();
                    }
                }
        );

        log.info("=== study_weekly_attachment 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudyWeeklyAttachment> createDummyStudyWeeklyAttachments() {
        final List<DummyStudyWeeklyAttachment> list = new ArrayList<>();
        for (int weekId = 1; weekId <= STUDY_SIZE; weekId++) {
            list.add(new DummyStudyWeeklyAttachment(weekId));
        }
        return list;
    }

    private void insertStudyWeeklySubmit() {
        log.info("=== study_weekly_submit 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO study_weekly_submit (week_id, participant_id, submit_type, upload_file_name, link)
                        VALUES (?, ?, ?, ?, ?);
                        """;
        final List<DummyStudyWeeklySubmit> dummyStudyWeeklySubmits = createDummyStudyWeeklySubmits();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudyWeeklySubmit dummyStudyWeeklySubmit = dummyStudyWeeklySubmits.get(i);
                        ps.setLong(1, dummyStudyWeeklySubmit.weekId());
                        ps.setLong(2, dummyStudyWeeklySubmit.participantId());
                        ps.setString(3, dummyStudyWeeklySubmit.submitType());
                        ps.setString(4, dummyStudyWeeklySubmit.uploadFileName());
                        ps.setString(5, dummyStudyWeeklySubmit.link());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudyWeeklySubmits.size();
                    }
                }
        );

        log.info("=== study_weekly_submit 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudyWeeklySubmit> createDummyStudyWeeklySubmits() {
        final List<DummyStudyWeeklySubmit> list = new ArrayList<>();
        for (int studyId = 1; studyId <= STUDY_SIZE; studyId++) {
            list.add(new DummyStudyWeeklySubmit(studyId, studyId));
            list.add(new DummyStudyWeeklySubmit(studyId, (studyId + 1) % MEMBER_SIZE));
            list.add(new DummyStudyWeeklySubmit(studyId, (studyId + 2) % MEMBER_SIZE));
            list.add(new DummyStudyWeeklySubmit(studyId, (studyId + 3) % MEMBER_SIZE));
            list.add(new DummyStudyWeeklySubmit(studyId, (studyId + 4) % MEMBER_SIZE));
        }
        return list;
    }

    private void insertStudyAttendance() {
        log.info("=== study_attendance 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO study_attendance (study_id, week, participant_id, status)
                        VALUES (?, ?, ?, ?);
                        """;
        final List<DummyStudyAttendance> dummyStudyAttendances = createDummyStudyAttendances();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudyAttendance dummyStudyAttendance = dummyStudyAttendances.get(i);
                        ps.setLong(1, dummyStudyAttendance.studyId());
                        ps.setInt(2, dummyStudyAttendance.week());
                        ps.setLong(3, dummyStudyAttendance.participantId());
                        ps.setString(4, dummyStudyAttendance.status());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudyAttendances.size();
                    }
                }
        );

        log.info("=== study_attendance 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudyAttendance> createDummyStudyAttendances() {
        final List<DummyStudyAttendance> list = new ArrayList<>();
        for (int studyId = 1; studyId <= STUDY_SIZE; studyId++) {
            list.add(new DummyStudyAttendance(studyId, studyId));
            list.add(new DummyStudyAttendance(studyId, (studyId + 1) % MEMBER_SIZE));
            list.add(new DummyStudyAttendance(studyId, (studyId + 2) % MEMBER_SIZE));
            list.add(new DummyStudyAttendance(studyId, (studyId + 3) % MEMBER_SIZE));
            list.add(new DummyStudyAttendance(studyId, (studyId + 4) % MEMBER_SIZE));
        }
        return list;
    }

    private void insertStudyFavorite() {
        log.info("=== favorite 더미 데이터 Insert ===");

        final String sql =
                """
                        INSERT INTO favorite (member_id, study_id)
                        VALUES (?, ?);
                        """;
        final List<DummyStudyFavorite> dummyStudyFavorites = createDummyStudyFavorites();
        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        final DummyStudyFavorite dummyStudyFavorite = dummyStudyFavorites.get(i);
                        ps.setLong(1, dummyStudyFavorite.memberId());
                        ps.setLong(2, dummyStudyFavorite.studyId());
                    }

                    @Override
                    public int getBatchSize() {
                        return dummyStudyFavorites.size();
                    }
                }
        );

        log.info("=== favorite 더미 데이터 Insert 완료 ===");
    }

    private List<DummyStudyFavorite> createDummyStudyFavorites() {
        final List<DummyStudyFavorite> list = new ArrayList<>();
        for (int memberId = 1; memberId <= MEMBER_SIZE; memberId++) {
            final int count = (int) (Math.random() * 11);
            for (int j = 0; j < count; j++) {
                final int studyId = (int) (Math.random() * STUDY_SIZE + 1);
                list.add(new DummyStudyFavorite(memberId, studyId));
            }
        }
        return list;
    }
}
