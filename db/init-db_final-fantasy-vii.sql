-- table used to store live updates of each ally as you play the game
create table if not exists final_fantasy_vii_allies
(
    name           text      not null,
    update_ts      timestamp not null,
    age            integer   not null,
    exp            bigint    not null,
    hp             integer   not null,
    mp             integer   not null,
    win_loss_ratio float     not null,
    ps_store_cost  float8,
    weapon         jsonb     not null,
    materia        jsonb,
    constraint final_fantasy_vii_allies_pk
        primary key (name, update_ts)
);

alter table final_fantasy_vii_allies
    owner to kafka_connect_user;
