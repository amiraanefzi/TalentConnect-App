CREATE DATABASE IF NOT EXISTS candidatures_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE candidatures_db;

CREATE TABLE IF NOT EXISTS candidatures (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    offer_id      BIGINT       NOT NULL,
    applicant_user_id BIGINT   NOT NULL,
    type          VARCHAR(30)  NOT NULL,
    status        VARCHAR(30)  NOT NULL DEFAULT 'SOUMISE',
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    cv_file_id    VARCHAR(80),
    referral_id   BIGINT,
    CONSTRAINT uk_candidature_applicant_offer UNIQUE (applicant_user_id, offer_id)
);

CREATE TABLE IF NOT EXISTS candidature_status_history (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidature_id BIGINT       NOT NULL,
    from_status    VARCHAR(30)  NOT NULL,
    to_status      VARCHAR(30)  NOT NULL,
    changed_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    changed_by     BIGINT       NOT NULL,
    changed_by_role VARCHAR(50) NOT NULL,
    CONSTRAINT fk_status_history_candidature FOREIGN KEY (candidature_id) REFERENCES candidatures (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notifications (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    type       VARCHAR(15)  NOT NULL,
    title      VARCHAR(200) NOT NULL,
    message    TEXT,
    is_read    BOOLEAN      NOT NULL DEFAULT FALSE,
    deep_link  VARCHAR(500),
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE INDEX IF NOT EXISTS idx_candidatures_applicant ON candidatures (applicant_user_id);
CREATE INDEX IF NOT EXISTS idx_candidatures_offer ON candidatures (offer_id);
CREATE INDEX IF NOT EXISTS idx_candidatures_status ON candidatures (status);
CREATE INDEX IF NOT EXISTS idx_status_history_cand ON candidature_status_history (candidature_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications (user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_read ON notifications (is_read);

SELECT 'candidatures_db OK' AS result;


