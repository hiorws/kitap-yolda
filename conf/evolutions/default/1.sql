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
  owner_users_id                bigint,
  addition_date                 date,
  constraint pk_books primary key (book_index)
);

create table transitions (
  transition_id                 bigserial not null,
  current_owner_users_id        bigint,
  is_arrived                    boolean default false not null,
  is_accepted                   boolean default false not null,
  arrival_date                  date,
  wish_date                     date,
  ship_date                     date,
  constraint pk_transitions primary key (transition_id)
);

create table transitions_books (
  transitions_transition_id     bigint not null,
  books_book_index              bigint not null,
  constraint pk_transitions_books primary key (transitions_transition_id,books_book_index)
);

create table transitions_users (
  transitions_transition_id     bigint not null,
  users_users_id                bigint not null,
  constraint pk_transitions_users primary key (transitions_transition_id,users_users_id)
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

alter table books add constraint fk_books_owner_users_id foreign key (owner_users_id) references users (users_id) on delete restrict on update restrict;
create index ix_books_owner_users_id on books (owner_users_id);

alter table transitions add constraint fk_transitions_current_owner_users_id foreign key (current_owner_users_id) references users (users_id) on delete restrict on update restrict;
create index ix_transitions_current_owner_users_id on transitions (current_owner_users_id);

alter table transitions_books add constraint fk_transitions_books_transitions foreign key (transitions_transition_id) references transitions (transition_id) on delete restrict on update restrict;
create index ix_transitions_books_transitions on transitions_books (transitions_transition_id);

alter table transitions_books add constraint fk_transitions_books_books foreign key (books_book_index) references books (book_index) on delete restrict on update restrict;
create index ix_transitions_books_books on transitions_books (books_book_index);

alter table transitions_users add constraint fk_transitions_users_transitions foreign key (transitions_transition_id) references transitions (transition_id) on delete restrict on update restrict;
create index ix_transitions_users_transitions on transitions_users (transitions_transition_id);

alter table transitions_users add constraint fk_transitions_users_users foreign key (users_users_id) references users (users_id) on delete restrict on update restrict;
create index ix_transitions_users_users on transitions_users (users_users_id);


# --- !Downs

alter table if exists books drop constraint if exists fk_books_adder_users_id;
drop index if exists ix_books_adder_users_id;

alter table if exists books drop constraint if exists fk_books_owner_users_id;
drop index if exists ix_books_owner_users_id;

alter table if exists transitions drop constraint if exists fk_transitions_current_owner_users_id;
drop index if exists ix_transitions_current_owner_users_id;

alter table if exists transitions_books drop constraint if exists fk_transitions_books_transitions;
drop index if exists ix_transitions_books_transitions;

alter table if exists transitions_books drop constraint if exists fk_transitions_books_books;
drop index if exists ix_transitions_books_books;

alter table if exists transitions_users drop constraint if exists fk_transitions_users_transitions;
drop index if exists ix_transitions_users_transitions;

alter table if exists transitions_users drop constraint if exists fk_transitions_users_users;
drop index if exists ix_transitions_users_users;

drop table if exists books cascade;

drop table if exists transitions cascade;

drop table if exists transitions_books cascade;

drop table if exists transitions_users cascade;

drop table if exists users cascade;

