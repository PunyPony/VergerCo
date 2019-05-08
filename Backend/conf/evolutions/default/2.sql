# --- Sample dataset

# --- !Ups
/*
id                  bigint not null,
  sunshine            boolean,
  temperature         float,
  humidity            float,
  wind                float,
  timeStamp           timestamp,
  constraint pk_weather primary key (id))*/

insert into WEATHER (id, sunshine, temperature, humidity, wind, timeStamp) values (1, true, 20., 55., 20., null);

# --- !Downs
delete from weather;