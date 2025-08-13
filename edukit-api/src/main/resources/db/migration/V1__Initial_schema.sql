-- Initial schema migration for Edukit application

-- Subject table (referenced by member)
create table subject
(
    subject_id bigint auto_increment primary key,
    name       varchar(255) not null
);

-- Member table
create table member
(
    member_id         bigint auto_increment primary key,
    member_uuid       varchar(255)                                 not null,
    email             varchar(255)                                 not null,
    nickname          varchar(255)                                 not null,
    password          varchar(500)                                 not null,
    role              enum ('ADMIN', 'PENDING_TEACHER', 'TEACHER') not null,
    school            enum ('HIGH_SCHOOL', 'MIDDLE_SCHOOL')        not null,
    subject_id        bigint                                       not null,
    profile_image_url varchar(500)                                 null,
    is_deleted        bit                                          not null,
    created_at        datetime(6)                                  not null,
    modified_at       datetime(6)                                  null,
    deleted_at        datetime(6)                                  null,
    verified_at       datetime(6)                                  null,
    constraint UQ_member_email unique (email),
    constraint UQ_member_nickname unique (nickname),
    constraint UQ_member_uuid unique (member_uuid),
    constraint FK_member_subject foreign key (subject_id) references subject (subject_id)
);

-- Valid email table
create table valid_email
(
    valid_email_id   bigint auto_increment primary key,
    education_office varchar(255) not null,
    valid_email      varchar(255) not null,
    constraint UQ_valid_email_email unique (valid_email)
);

-- Verification code table
create table verification_code
(
    verification_code_id bigint auto_increment primary key,
    member_id            bigint                                          not null,
    verification_code    varchar(255)                                    not null,
    type                 enum ('PASSWORD_RESET', 'TEACHER_VERIFICATION') not null,
    status               enum ('VERIFIED', 'EXPIRED', 'PENDING')         not null,
    created_at           datetime(6)                                     not null,
    expired_at           datetime(6)                                     not null,
    constraint FK_verification_code_member foreign key (member_id) references member (member_id)
);

-- Nickname banned word table
create table nickname_banned_word
(
    nickname_banned_word_id bigint auto_increment primary key,
    word                    varchar(255) not null,
    created_at              datetime(6)  not null,
    modified_at             datetime(6)  null
);

-- Notice table
create table notice
(
    notice_id   bigint auto_increment primary key,
    title       varchar(255)                          not null,
    content     text                                  not null,
    category    enum ('ALL', 'ANNOUNCEMENT', 'EVENT') not null,
    is_deleted  bit                                   not null,
    created_at  datetime(6)                           not null,
    modified_at datetime(6)                           null,
    deleted_at  datetime(6)                           null
);

-- Notice file table
create table notice_file
(
    notice_file_id bigint auto_increment primary key,
    notice_id      bigint       null,
    file_path      varchar(500) not null,
    created_at     datetime(6)  not null,
    modified_at    datetime(6)  null,
    constraint FK_notice_file_notice foreign key (notice_id) references notice (notice_id)
);

-- Post table
create table post
(
    post_id     bigint auto_increment primary key,
    member_id   bigint       not null,
    subject_id  bigint       not null,
    title       varchar(255) not null,
    content     text         not null,
    category    enum ('TMP') not null,
    like_count  int          not null,
    created_at  datetime(6)  not null,
    modified_at datetime(6)  null,
    constraint FK_post_member foreign key (member_id) references member (member_id),
    constraint FK_post_subject foreign key (subject_id) references subject (subject_id)
);

-- Post comment table
create table post_comment
(
    post_comment_id   bigint auto_increment primary key,
    post_id           bigint       not null,
    member_id         bigint       not null,
    parent_comment_id bigint       not null,
    content           varchar(255) not null,
    created_at        datetime(6)  not null,
    modified_at       datetime(6)  null,
    constraint FK_post_comment_member foreign key (member_id) references member (member_id),
    constraint FK_post_comment_post foreign key (post_id) references post (post_id)
);

-- Post file table
create table post_file
(
    post_file_id bigint auto_increment primary key,
    post_id      bigint                       not null,
    file_name    varchar(255)                 not null,
    file_path    varchar(500)                 not null,
    file_size    bigint                       not null,
    category     enum ('ORIGINAL', 'PREVIEW') not null,
    created_at   datetime(6)                  not null,
    constraint FK_post_file_post foreign key (post_id) references post (post_id)
);

-- Post like table
create table post_like
(
    post_like_id bigint auto_increment primary key,
    member_id    bigint      not null,
    post_id      bigint      not null,
    created_at   datetime(6) not null,
    constraint FK_post_like_member foreign key (member_id) references member (member_id),
    constraint FK_post_like_post foreign key (post_id) references post (post_id)
);

-- Student table
create table student
(
    student_id     bigint auto_increment primary key,
    member_id      bigint       not null,
    student_name   varchar(255) not null,
    student_number varchar(255) not null,
    grade          varchar(255) not null,
    class_number   varchar(255) not null,
    created_at     datetime(6)  not null,
    modified_at    datetime(6)  null,
    constraint FK_student_member foreign key (member_id) references member (member_id)
);

-- Student record table
create table student_record
(
    student_record_id   bigint auto_increment primary key,
    student_id          bigint                                                 not null,
    student_record_type enum ('BEHAVIOR', 'CAREER', 'CLUB', 'FREE', 'SUBJECT') not null,
    description         text                                                   not null,
    created_at          datetime(6)                                            not null,
    modified_at         datetime(6)                                            null,
    constraint FK_student_record_student foreign key (student_id) references student (student_id)
);

-- Student record AI task table
create table student_record_ai_task
(
    student_record_ai_task_id bigint auto_increment primary key,
    student_record_id         bigint                                                not null,
    prompt                    mediumtext                                            not null,
    status                    enum ('VERIFIED', 'FAILED', 'IN_PROGRESS', 'PENDING') not null,
    started_at                datetime(6)                                           null,
    completed_at              datetime(6)                                           null,
    constraint FK_ai_task_to_record foreign key (student_record_id) references student_record (student_record_id)
);

-- Student record AI result table
create table student_record_ai_result
(
    student_record_ai_result_id bigint auto_increment primary key,
    student_record_ai_task_id   bigint       not null,
    result                      varchar(255) not null,
    created_at                  datetime(6)  null,
    constraint FK_ai_result_to_task foreign key (student_record_ai_task_id) references student_record_ai_task (student_record_ai_task_id)
);

