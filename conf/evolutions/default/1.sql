# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table books (
  id                            bigserial not null,
  isbn                          varchar(255),
  name                          varchar(255),
  is_available                  boolean default false not null,
  author                        varchar(255),
  addition_date                 date,
  constraint pk_books primary key (id)
);

create table users (
  id                            bigserial not null,
  username                      varchar(255),
  password                      varchar(255),
  name                          varchar(255),
  email                         varchar(255),
  constraint pk_users primary key (id)
);


# --- !Downs

drop table if exists books cascade;

drop table if exists users cascade;

