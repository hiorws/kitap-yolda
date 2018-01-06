# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table books (
  book_index                    bigserial not null,
  isbn                          varchar(255),
  name                          varchar(255),
  is_available                  boolean default false not null,
  author                        varchar(255),
  adder_users_id                bigint,
  addition_date                 date,
  constraint pk_books primary key (book_index)
);

create table users (
  users_id                      bigserial not null,
  username                      varchar(255),
  password                      varchar(255),
  name                          varchar(255),
  email                         varchar(255),
  constraint pk_users primary key (users_id)
);

alter table books add constraint fk_books_adder_users_id foreign key (adder_users_id) references users (users_id) on delete restrict on update restrict;
create index ix_books_adder_users_id on books (adder_users_id);


# --- !Downs

alter table if exists books drop constraint if exists fk_books_adder_users_id;
drop index if exists ix_books_adder_users_id;

drop table if exists books cascade;

drop table if exists users cascade;

