
# --- First database schema
# --- !Ups
set ignorecase true;

create table weather (
  id                  bigint not null,
  sunshine            boolean,
  temperature         float,
  humidity            float,
  wind                float,
  timeStamp           timestamp,
  constraint pk_weather primary key (id))
;

/*
case class Weather(id: Option[Long] = None,
                    sunshine: Option[Boolean],
                    temperature: Option[Float],
                    humidity: Option[Float],
                    wind: Option[Float],
                    timeStamp: Option[Date])*/

create sequence weather_seq start with 1000;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists weather;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists weather_seq;
