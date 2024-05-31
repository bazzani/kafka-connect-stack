--
-- Add seed data for Cloud winning a battle
insert into final_fantasy_vii_db.final_fantasy_vii_battle_outcome (battle_complete_time, win_or_loss, allies, exp_earned, ap_earned)
values ('2024-06-04 18:37:44', 1, 'Cloud', 100, 15);

insert into final_fantasy_vii_db.final_fantasy_vii_battle_outcome (battle_complete_time, win_or_loss, allies, exp_earned, ap_earned)
values (UTC_TIMESTAMP(), 1, 'Cloud, Tifa', 50, 5);
