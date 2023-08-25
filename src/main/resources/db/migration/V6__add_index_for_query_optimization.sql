-- 스터디 찜 관련 쿼리 최적화
ALTER TABLE favorite
    ADD INDEX idx_favorite_member_id_study_id (member_id, study_id);

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
