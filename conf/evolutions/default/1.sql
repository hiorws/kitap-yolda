# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table books (
  book_index                    bigserial not null,
  isbn                          varchar(255),
  condition                     varchar(255),
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
  sender_users_id               bigint,
  receiver_users_id             bigint,
  book_book_index               bigint,
  state                         integer not null,
  transition_is_active          boolean default false not null,
  ship_date                     date,
  wish_date                     date,
  constraint pk_transitions primary key (transition_id)
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
  address                       varchar(255),
  constraint pk_users primary key (users_id)
);

alter table books add constraint fk_books_adder_users_id foreign key (adder_users_id) references users (users_id) on delete restrict on update restrict;
create index ix_books_adder_users_id on books (adder_users_id);

alter table books add constraint fk_books_owner_users_id foreign key (owner_users_id) references users (users_id) on delete restrict on update restrict;
create index ix_books_owner_users_id on books (owner_users_id);

alter table transitions add constraint fk_transitions_sender_users_id foreign key (sender_users_id) references users (users_id) on delete restrict on update restrict;
create index ix_transitions_sender_users_id on transitions (sender_users_id);

alter table transitions add constraint fk_transitions_receiver_users_id foreign key (receiver_users_id) references users (users_id) on delete restrict on update restrict;
create index ix_transitions_receiver_users_id on transitions (receiver_users_id);

alter table transitions add constraint fk_transitions_book_book_index foreign key (book_book_index) references books (book_index) on delete restrict on update restrict;
create index ix_transitions_book_book_index on transitions (book_book_index);

alter table transitions_users add constraint fk_transitions_users_transitions foreign key (transitions_transition_id) references transitions (transition_id) on delete restrict on update restrict;
create index ix_transitions_users_transitions on transitions_users (transitions_transition_id);

alter table transitions_users add constraint fk_transitions_users_users foreign key (users_users_id) references users (users_id) on delete restrict on update restrict;
create index ix_transitions_users_users on transitions_users (users_users_id);


# --- !Downs

alter table if exists books drop constraint if exists fk_books_adder_users_id;
drop index if exists ix_books_adder_users_id;

alter table if exists books drop constraint if exists fk_books_owner_users_id;
drop index if exists ix_books_owner_users_id;

alter table if exists transitions drop constraint if exists fk_transitions_sender_users_id;
drop index if exists ix_transitions_sender_users_id;

alter table if exists transitions drop constraint if exists fk_transitions_receiver_users_id;
drop index if exists ix_transitions_receiver_users_id;

alter table if exists transitions drop constraint if exists fk_transitions_book_book_index;
drop index if exists ix_transitions_book_book_index;

alter table if exists transitions_users drop constraint if exists fk_transitions_users_transitions;
drop index if exists ix_transitions_users_transitions;

alter table if exists transitions_users drop constraint if exists fk_transitions_users_users;
drop index if exists ix_transitions_users_users;

drop table if exists books cascade;

drop table if exists transitions cascade;

drop table if exists transitions_users cascade;

drop table if exists users cascade;

