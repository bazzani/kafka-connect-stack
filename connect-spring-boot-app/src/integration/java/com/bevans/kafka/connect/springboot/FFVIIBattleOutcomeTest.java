package com.bevans.kafka.connect.springboot;

import com.bevans.kafka.connect.springboot.data.FFVIIBattleOutcomeRepository;
import com.bevans.kafka.connect.springboot.ffvii.FFVIIBattleOutcomeCreator;
import com.bevans.kafka.connect.springboot.kafka.KafkaProducer;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.apache.kafka.common.utils.Utils.sleep;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class FFVIIBattleOutcomeTest {
    private final KafkaProducer kafkaProducer;
    private final FFVIIBattleOutcomeRepository ffviiBattleOutcomeRepository;
    private final FFVIIBattleOutcomeCreator ffviiBattleOutcomeCreator;

    @Autowired
    public FFVIIBattleOutcomeTest(KafkaProducer kafkaProducer,
                                  FFVIIBattleOutcomeRepository ffviiBattleOutcomeRepository,
                                  FFVIIBattleOutcomeCreator ffviiBattleOutcomeCreator) {
        this.kafkaProducer = kafkaProducer;
        this.ffviiBattleOutcomeRepository = ffviiBattleOutcomeRepository;
        this.ffviiBattleOutcomeCreator = ffviiBattleOutcomeCreator;
    }

    @BeforeEach
    public void setup() {
        ffviiBattleOutcomeRepository.deleteAll();
    }

    @Test
    void shouldSendBattleOutcomeAndFindInDatabase() {
        // given
        var testStartTime = LocalDateTime.now(ZoneId.of("UTC"));
        var avroData = ffviiBattleOutcomeCreator.getAvroData();
        var avroRecord = this.ffviiBattleOutcomeCreator.createAvroRecord();

        // when
        kafkaProducer.send("ff_vii.battle_outcomes.v1", "key", avroRecord);
        sleep(2000);

        var battleOutcomeFromDatabase = ffviiBattleOutcomeRepository.findAll();

        // then
        assertThatList(battleOutcomeFromDatabase).hasSize(1);
        var battleOutcome = battleOutcomeFromDatabase.get(0);

        assertAll(
                () -> assertThat(battleOutcome.getBattleCompleteTime()).isAfter(testStartTime),
                () -> assertThat(battleOutcome.getWinOrLoss()).isEqualTo(avroData.getWinOrLoss()),
                () -> assertThat(battleOutcome.getAllies()).isEqualTo(getJsonStringArrayWithSpaces(avroData.getAllies())),
                () -> assertThat(battleOutcome.getExpEarned()).isEqualTo(avroData.getExpEarned()),
                () -> assertThat(battleOutcome.getApEarned()).isEqualTo(avroData.getApEarned())
        );
    }

    private String getJsonStringArrayWithSpaces(List<String> stringList) {
        var jsonStringArray = new JSONArray(stringList).toString();
        return jsonStringArray.replace(",", ", ");
    }
}
