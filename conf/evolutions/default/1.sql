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

create table transitions (
  transition_id                 bigserial not null,
  book_index                    bigint,
  user_id                       bigint,
  is_arrived                    boolean default false not null,
  is_accepted                   boolean default false not null,
  arrival_date                  date,
  wish_date                     date,
  ship_date                     date,
  constraint uq_transitions_book_index unique (book_index),
  constraint uq_transitions_user_id unique (user_id),
  constraint pk_transitions primary key (transition_id)
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

alter table transitions add constraint fk_transitions_book_index foreign key (book_index) references books (book_index) on delete restrict on update restrict;

alter table transitions add constraint fk_transitions_user_id foreign key (user_id) references users (users_id) on delete restrict on update restrict;


# --- !Downs

alter table if exists books drop constraint if exists fk_books_adder_users_id;
drop index if exists ix_books_adder_users_id;

alter table if exists transitions drop constraint if exists fk_transitions_book_index;

alter table if exists transitions drop constraint if exists fk_transitions_user_id;

drop table if exists books cascade;

drop table if exists transitions cascade;

drop table if exists users cascade;

