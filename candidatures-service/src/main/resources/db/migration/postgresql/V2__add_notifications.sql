-- V2 : Ajout table notifications
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(15) NOT NULL,    -- NOSONAR: VARCHAR is correct PostgreSQL syntax (rule targets Oracle/PL-SQL only)
    title VARCHAR(200) NOT NULL,  -- NOSONAR
    message TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    deep_link VARCHAR(500),       -- NOSONAR
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_notifications_user_id ON notifications (user_id);
CREATE INDEX idx_notifications_is_read ON notifications (is_read);

