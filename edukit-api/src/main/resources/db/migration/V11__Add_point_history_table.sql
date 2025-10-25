-- Add point_history table for tracking all point transactions
create table point_history
(
    point_history_id bigint auto_increment primary key,
    member_id        bigint                                                                 not null,
    transaction_type enum ('CHARGE', 'DEDUCT', 'REFUND', 'COMPENSATION', 'ADMIN_ADJUST')   not null,
    amount           int                                                                    not null,
    task_id          bigint                                                                 null,
    created_at       datetime(6)                                                            not null,
    modified_at      datetime(6)                                                            null,
    constraint FK_point_history_member foreign key (member_id) references member (member_id),
    constraint CHK_point_history_amount_positive check (amount > 0)
);

-- Composite index for member's recent history queries (most common use case)
create index IDX_point_history_member_created on point_history (member_id, created_at desc);

-- Unique index for idempotency: prevent duplicate transactions for same task
-- Note: MySQL allows multiple NULL values in UNIQUE index, so CHARGE/REFUND with NULL task_id can have duplicates
create unique index UQ_point_history_task_transaction on point_history (task_id, transaction_type);

-- Index for task-based queries (finding all transactions for a specific task)
create index IDX_point_history_task_id on point_history (task_id);
