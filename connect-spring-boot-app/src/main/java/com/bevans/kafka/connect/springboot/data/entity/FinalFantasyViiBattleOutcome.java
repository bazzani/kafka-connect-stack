package com.bevans.kafka.connect.springboot.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "final_fantasy_vii_battle_outcome")
@Getter
public class FinalFantasyViiBattleOutcome {
    @Id
    @Column(name = "battle_complete_time")
    private LocalDateTime battleCompleteTime;

    @Column(name = "win_or_loss")
    private Boolean winOrLoss;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "allies")
    private String allies;

    @Column(name = "exp_earned")
    private Integer expEarned;

    @Column(name = "ap_earned")
    private Integer apEarned;
}
