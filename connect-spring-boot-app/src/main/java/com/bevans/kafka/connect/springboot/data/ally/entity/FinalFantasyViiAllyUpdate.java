package com.bevans.kafka.connect.springboot.data.ally.entity;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Table(name = "final_fantasy_vii_ally_update")
@IdClass(FinalFantasyViiAllyUpdatePrimaryKey.class)
@Getter
public class FinalFantasyViiAllyUpdate {
    @Id
    @Column(name = "name")
    private String name;

    @Id
    @Column(name = "update_ts")
    private String updateTs;

    @Column(name = "age")
    private Long age;

    @Column(name = "exp")
    private Long exp;

    @Column(name = "hp")
    private Long hp;

    @Column(name = "mp")
    private Long mp;

    @Column(name = "win_loss_ratio")
    private Double winLossRatio;

    @Column(name = "ps_store_cost")
    private Double psStoreCost;

    @Column(name = "weapon")
    private String weapon;

    @Column(name = "materia")
    private String materia;
}
