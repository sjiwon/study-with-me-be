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

-- 스터디 출석 정보 조회 쿼리 최적화
ALTER TABLE study_attendance
    ADD INDEX idx_study_attendance_study_id_week (study_id, week);

-- 스터디 메인페이지 조회 쿼리 최적화
ALTER TABLE study
    ADD INDEX idx_study_category_is_terminated (category, is_terminated);
ALTER TABLE study
    ADD INDEX idx_study_study_type_category_is_terminated (study_type, category, is_terminated);
ALTER TABLE study
    ADD INDEX idx_study_province_city_category_is_terminated (province, city, category, is_terminated);
ALTER TABLE study
    ADD INDEX idx_study_province_city_study_type_category_is_terminated (province, city, study_type, category, is_terminated);
