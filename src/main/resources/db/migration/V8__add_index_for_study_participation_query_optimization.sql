-- 신청자 Validation 관련 쿼리 최적화
ALTER TABLE study_participant
    ADD INDEX idx_participant_study_id_member_id_status (study_id, member_id, status);

-- 스터디 해시태그 조회 쿼리 최적화
ALTER TABLE study_hashtag
    ADD INDEX idx_hashtag_study_id_name (study_id, name);
