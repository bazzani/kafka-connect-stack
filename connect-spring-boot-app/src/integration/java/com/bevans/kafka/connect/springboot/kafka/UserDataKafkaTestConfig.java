package com.bevans.kafka.connect.springboot.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@TestConfiguration
@EnableKafka
public class UserDataKafkaTestConfig {
    static final String USER_DATA_TOPIC_NAME = "jdbc-source-mysql-Json-Schema__user_data";

    @Bean
    public UserDataKafkaConsumerFixture kafkaUserDataConsumerFixture() {
        return new UserDataKafkaConsumerFixture();
    }

    @Bean
    public NewTopic userDataTopic() {
        return TopicBuilder.name(USER_DATA_TOPIC_NAME)
                .build();
    }
}
