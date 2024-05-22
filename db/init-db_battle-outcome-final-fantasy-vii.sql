-- table used to store live updates of battle outcomes as you play the game
create table if not exists final_fantasy_vii_battle_outcome
(
    battle_complete_time timestamp not null,
    win_or_loss          boolean   not null,
    allies               jsonb     not null,
    exp_earned           integer   not null,
    ap_earned            integer   not null,
    constraint final_fantasy_vii_battle_outcome_pk
        primary key (battle_complete_time)
);

alter table final_fantasy_vii_battle_outcome
    owner to kafka_connect_user;
