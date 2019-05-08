# --- First database schema
# --- !Ups
set ignorecase true;

create table weather
(
  id          bigint not null,
  objectID    bigint,
  sunshine    boolean,
  temperature float,
  humidity    float,
  wind        float,
  timeStamp   timestamp,
  constraint pk_weather primary key (id)
);

create table state
(
  id          bigint not null,
  objectID    bigint,
  chargeperc  float,
  temperature float,
  placename   varchar(64),
  lat         float,
  long        float,
  timeStamp   timestamp,
  constraint pk_state primary key (id)
)
;

create table fruitquality
(
  id        bigint not null,
  objectID  bigint,
  mature    boolean,
  sickness  boolean,
  timeStamp timestamp,
  constraint pk_fruitquality primary key (id)
)
;

create table alert
(
  id        bigint not null,
  objectID  bigint,
  alertType varchar(128),
  timeStamp timestamp,
  constraint pk_alert primary key (id)
)
;

create sequence weather_seq start with 1000;
create sequence state_seq start with 1000;
create sequence fruitquality_seq start with 1000;
create sequence alert_seq start with 1000;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists weather;
drop table if exists state;
drop table if exists fruitquality;
drop table if exists alert;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists weather_seq;
drop sequence if exists state_seq;
drop sequence if exists fruitquality_seq;
drop sequence if exists alert_seq;
