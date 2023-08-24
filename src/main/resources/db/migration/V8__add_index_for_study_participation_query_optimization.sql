-- 신청자 Validation 관련 쿼리 최적화
ALTER TABLE study_participant
    ADD INDEX idx_participant_member_id_status_study_id (member_id, status, study_id);
