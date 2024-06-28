package com.bevans.kafka.connect.springboot.data.ally;

import com.bevans.kafka.connect.springboot.data.ally.entity.FinalFantasyViiAllyUpdate;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface FFVIIAllyUpdateRepository extends Repository<FinalFantasyViiAllyUpdate, String> {
    List<FinalFantasyViiAllyUpdate> findAllByName(String name);
}
