package com.bevans.kafka.connect.springboot.kafka;

import com.bevans.kafka.connect.springboot.content.ContentLoadException;
import com.bevans.kafka.connect.springboot.content.ContentLoader;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientFactory;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.schemaregistry.json.JsonSchema;
import io.confluent.kafka.schemaregistry.json.JsonSchemaProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestConfiguration
@EnableKafka
@Slf4j
public class KafkaUserDataTestConfiguration {
    static final String USER_DATA_TOPIC_NAME = "jdbc-source-mysql-Json-Schema__user_data";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Value("classpath:json/user-data_schema.json")
    private Resource jsonSchemaFile;

    @Bean
    public KafkaUserDataConsumerFixture kafkaUserDataConsumerFixture() {
        return new KafkaUserDataConsumerFixture();
    }

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic userDataTopic() {
        return TopicBuilder.name(USER_DATA_TOPIC_NAME)
                .build();
    }

    @Bean
    public SchemaRegistryClient schemaRegistryClient(ContentLoader contentLoader) {
        var schemaRegistryClient = SchemaRegistryClientFactory.newClient(
                List.of(schemaRegistryUrl),
                1000,
                Collections.singletonList(new JsonSchemaProvider()),
                Map.of(),
                Map.of()
        );

        try {
            var jsonSchemaString = contentLoader.getStringFromResource(jsonSchemaFile);
            var jsonSchema = new JsonSchema(jsonSchemaString);
            schemaRegistryClient.register(USER_DATA_TOPIC_NAME + "-value", jsonSchema);
        } catch (ContentLoadException | RestClientException | IOException e) {
            log.error(e.getMessage(), e);
        }

        return schemaRegistryClient;
    }
}
