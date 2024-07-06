package com.bevans.kafka.connect.springboot.user;

import com.bevans.kafka.connect.springboot.data.user.UserData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static com.bevans.kafka.connect.springboot.user.UserDataKafkaTestConfig.USER_DATA_TOPIC_NAME;

@Slf4j
public class UserDataKafkaConsumerFixture {
    private final List<ConsumerRecord<String, LinkedHashMap<String, Object>>> received = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = USER_DATA_TOPIC_NAME, groupId = "user-data-test-consumer-group")
    public void listenForUserData(ConsumerRecord<String, LinkedHashMap<String, Object>> message) {
        received.add(message);
        log.info("{} Received message from Kafka topic [{}] = {}", getLogTimePrefix(), message.topic(), message);
    }

    public Optional<UserData> getLatestUserData() {
        if (received.isEmpty()) {
            log.info("{} latestRecord from Kafka missing", getLogTimePrefix());

            return Optional.empty();
        } else {
            var latestRecord = received.get(received.size() - 1);
            log.info("{} latestRecord from Kafka = {}", getLogTimePrefix(), latestRecord);

            var userFromTopic = objectMapper.convertValue(latestRecord.value(), UserData.class);

            return Optional.of(userFromTopic);
        }
    }

    public void clearRecords() {
        received.clear();
    }

    private String getLogTimePrefix() {
        return LocalDateTime.now() + " ::";
    }
}
