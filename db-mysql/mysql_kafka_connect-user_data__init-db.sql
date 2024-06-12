create database if not exists kafka_connect;

create table if not exists kafka_connect.user_data
(
    id   int auto_increment,
    name text not null,
    age  int  not null,
    primary key (id)
);
