-- 스터디 찜 관련 쿼리 최적화
ALTER TABLE favorite
    ADD INDEX idx_favorite_member_id_study_id (member_id, study_id);

-- 사용자 정보 조회 -> 관심사 조회 쿼리 최적화
ALTER TABLE member_interest
    ADD INDEX idx_interest_member_id_category (member_id, category);

-- 사용자가 신청한/참여중인 스터디 조회 쿼리 최적화
ALTER TABLE study_participant
    ADD INDEX idx_participant_member_id_status (member_id, status);

-- 사용자가 졸업한 스터디 -> 작성한 리뷰 조회 쿼리 최적화
ALTER TABLE study_review
    ADD INDEX idx_study_review_writer_id (writer_id);
