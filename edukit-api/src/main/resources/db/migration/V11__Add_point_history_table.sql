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
    constraint FK_point_history_member foreign key (member_id) references member (member_id)
);

-- Add index on member_id for faster query performance
create index IDX_point_history_member_id on point_history (member_id);

-- Add index on task_id for tracking AI task-related transactions
create index IDX_point_history_task_id on point_history (task_id);

-- Add index on created_at for time-based queries
create index IDX_point_history_created_at on point_history (created_at);
