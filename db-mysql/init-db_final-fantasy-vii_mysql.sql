create database if not exists final_fantasy_vii_db;

-- table used to store live updates of battle outcomes as you play the game
create table if not exists final_fantasy_vii_db.final_fantasy_vii_battle_outcome
(
    id                   int auto_increment,
    battle_complete_time timestamp  not null,
    win_or_loss          tinyint(1) not null,
    allies               text       not null,
    exp_earned           int        not null,
    ap_earned            int        not null,

    primary key (id, battle_complete_time)
);
