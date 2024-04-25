package com.bevans.kafka.connect.springboot;

import com.bevans.kafka.connect.springboot.data.FFVIIAllyUpdateRepository;
import com.bevans.kafka.connect.springboot.ffvii.FFVIIAllyUpdateCreator;
import com.bevans.kafka.connect.springboot.kafka.KafkaProducer;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.apache.kafka.common.utils.Utils.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class FFVIIAllyUpdateTest {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private FFVIIAllyUpdateRepository ffviiAllyUpdateRepository;

    @Autowired
    private FFVIIAllyUpdateCreator ffviiAllyUpdateCreator;

    @Test
    void shouldSendAllyUpdateAndFindInDatabase() {
        // given
        var avroData = ffviiAllyUpdateCreator.getAvroData();

        var now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        var avroRecord = this.ffviiAllyUpdateCreator.createAvroRecord(now);
        var createdName = (String) avroRecord.get("name");

        // when
        kafkaProducer.send("ff_vii.ally_updates.v1", "key", avroRecord);
        sleep(2000);
        var allyUpdateFromDatabase = ffviiAllyUpdateRepository.findAllByName(createdName);

        // then
        assertThatList(allyUpdateFromDatabase).hasSize(1);
        var allyUpdate = allyUpdateFromDatabase.get(0);

        assertAll(
                () -> assertThat(allyUpdate.getName()).isEqualTo(createdName),
                () -> assertThat(allyUpdate.getUpdateTs()).isNotNull(),
                () -> assertThat(allyUpdate.getAge()).isEqualTo(avroData.getAge()),
                () -> assertThat(allyUpdate.getExp()).isEqualTo(avroData.getExp()),
                () -> assertThat(allyUpdate.getHp()).isEqualTo(avroData.getHp()),
                () -> assertThat(allyUpdate.getMp()).isEqualTo(avroData.getMp()),
                () -> assertThat(allyUpdate.getWinLossRatio()).isEqualTo(avroData.getWinLossRatio()),
                () -> assertThat(allyUpdate.getPsStoreCost()).isEqualTo(avroData.getPsStoreCost()),
                () -> assertThat(allyUpdate.getWeapon()).isEqualTo(avroData.getWeapon().toString()),
                () -> assertThat(allyUpdate.getMateria()).isEqualTo(getJsonStringArrayWithSpaces(avroData.getMateria()))
        );
    }

    private String getJsonStringArrayWithSpaces(List<String> stringList) {
        var jsonStringArray = new JSONArray(stringList).toString();
        return jsonStringArray.replace(",", ", ");
    }
}
