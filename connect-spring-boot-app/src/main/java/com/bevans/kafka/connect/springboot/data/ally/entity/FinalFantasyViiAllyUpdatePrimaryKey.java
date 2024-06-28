package com.bevans.kafka.connect.springboot.data.ally.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class FinalFantasyViiAllyUpdatePrimaryKey implements Serializable {
    @Id
    @Column(name = "name")
    private String name;

    @Id
    @Column(name = "update_ts")
    private String updateTs;
}
