-- V4: Relax audit_log columns to allow failed login entries without a user/target
ALTER TABLE audit_log
  MODIFY COLUMN actor_user_id BIGINT NULL,
  MODIFY COLUMN target_id BIGINT NULL;

