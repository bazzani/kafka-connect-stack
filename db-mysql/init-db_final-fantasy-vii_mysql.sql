create database if not exists final_fantasy_vii_db;

-- table used to store live updates of battle outcomes as you play the game
create table if not exists final_fantasy_vii_db.final_fantasy_vii_battle_outcome
(
    id   int auto_increment,
    name text not null,
    exp  int  not null,
    ap   int  not null,

    primary key (id)
);
