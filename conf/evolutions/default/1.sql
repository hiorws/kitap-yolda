# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table reader (
  id                            bigserial not null,
  name                          varchar(255),
  password                      varchar(255),
  constraint pk_reader primary key (id)
);


# --- !Downs

drop table if exists reader cascade;

