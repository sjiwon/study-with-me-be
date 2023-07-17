package com.kgu.studywithme.common;

import com.kgu.studywithme.auth.domain.TokenRepository;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.memberreport.domain.MemberReportRepository;
import com.kgu.studywithme.memberreview.domain.MemberReviewRepository;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
import com.kgu.studywithme.study.domain.notice.comment.CommentRepository;
import com.kgu.studywithme.study.domain.review.ReviewRepository;
import com.kgu.studywithme.study.domain.week.WeekRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public abstract class ServiceTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected TokenRepository tokenRepository;

    @Autowired
    protected StudyRepository studyRepository;

    @Autowired
    protected FavoriteRepository favoriteRepository;

    @Autowired
    protected NoticeRepository noticeRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected MemberReportRepository memberReportRepository;

    @Autowired
    protected AttendanceRepository attendanceRepository;

    @Autowired
    protected MemberReviewRepository memberReviewRepository;

    @Autowired
    protected WeekRepository weekRepository;

    @AfterEach
    void clearDatabase() {
        databaseCleaner.cleanUpDatabase();
    }
}
