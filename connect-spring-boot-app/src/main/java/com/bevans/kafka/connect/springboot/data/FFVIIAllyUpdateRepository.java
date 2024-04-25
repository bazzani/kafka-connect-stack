package com.bevans.kafka.connect.springboot.data;

import com.bevans.kafka.connect.springboot.data.entity.FinalFantasyViiAllyUpdate;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface FFVIIAllyUpdateRepository extends Repository<FinalFantasyViiAllyUpdate, String> {
    List<FinalFantasyViiAllyUpdate> findAllByName(String name);
}
