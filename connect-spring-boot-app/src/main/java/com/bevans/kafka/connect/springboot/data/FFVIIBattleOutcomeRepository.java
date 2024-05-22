package com.bevans.kafka.connect.springboot.data;

import com.bevans.kafka.connect.springboot.data.entity.FinalFantasyViiBattleOutcome;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FFVIIBattleOutcomeRepository extends CrudRepository<FinalFantasyViiBattleOutcome, LocalDateTime> {
    List<FinalFantasyViiBattleOutcome> findAllByBattleCompleteTimeAfter(LocalDateTime cutoff);

    @Override
    List<FinalFantasyViiBattleOutcome> findAll();
}
