-- Project Name : demo-postgresql
-- Date/Time    : 2025/03/29 19:06:17
-- Author       : mkaneyama
-- RDBMS Type   : PostgreSQL
-- Application  : A5:SQL Mk-2

/*
  << 注意！！ >>
  BackupToTempTable, RestoreFromTempTable疑似命令が付加されています。
  これにより、drop table, create table 後もデータが残ります。
  この機能は一時的に $$TableName のような一時テーブルを作成します。
  この機能は A5:SQL Mk-2でのみ有効であることに注意してください。
*/

-- UserTable
-- * BackupToTempTable
drop table if exists tbl_user cascade;

-- * RestoreFromTempTable
create table tbl_user (
  user_id bigserial not null
  , user_key uuid not null
  , user_type user_type_enum not null
  , username character varying(512) not null
  , password character varying(512) not null
  , user_profile jsonb not null
  , created_at timestamp with time zone not null
  , updated_at timestamp with time zone not null
) ;

create unique index tbl_user_uk1
  on tbl_user(user_key);

create unique index tbl_user_uk2
  on tbl_user(username);

create unique index tbl_user_PKI
  on tbl_user(user_id);

alter table tbl_user
  add constraint tbl_user_PKC primary key (user_id);

comment on table tbl_user is 'UserTable';
comment on column tbl_user.user_id is 'UserId';
comment on column tbl_user.user_key is 'UserKey(UK1)';
comment on column tbl_user.user_type is 'UserType:user, admin';
comment on column tbl_user.username is 'Username(UK2)';
comment on column tbl_user.password is 'Password';
comment on column tbl_user.user_profile is 'UserProfile';
comment on column tbl_user.created_at is 'CreatedAt';
comment on column tbl_user.updated_at is 'UpdatedAt';

