-- Add point column to member table
alter table member
    add column point int not null default 1000;
