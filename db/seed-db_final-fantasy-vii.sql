--
-- Add seed data for Cloud, the main character in the story
insert into final_fantasy_vii_allies (name, update_ts, age, exp, hp, mp, win_loss_ratio, ps_store_cost, weapon, materia)
values ('Cloud Strife', '2024-04-05 22:35:00.000000', 30, 32000, 5999, 128, 12.12345, 123.1234567, '{
  "name": "Buster Sword",
  "attack": 22,
  "magic_attack": 22
}', '[
  "Healing",
  "Fire",
  "Raise"
]');
