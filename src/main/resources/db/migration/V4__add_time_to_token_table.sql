ALTER TABLE member_token
    ADD created_at DATETIME NOT NULL;

ALTER TABLE member_token
    ADD last_modified_at DATETIME NOT NULL;
