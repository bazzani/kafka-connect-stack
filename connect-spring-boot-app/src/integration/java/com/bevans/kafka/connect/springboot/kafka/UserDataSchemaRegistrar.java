package com.bevans.kafka.connect.springboot.kafka;

import com.bevans.kafka.connect.springboot.content.ContentLoadException;
import com.bevans.kafka.connect.springboot.content.ContentLoader;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.schemaregistry.json.JsonSchema;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.bevans.kafka.connect.springboot.kafka.UserDataKafkaTestConfig.USER_DATA_TOPIC_NAME;

@Component
@Slf4j
public class UserDataSchemaRegistrar {
    private final ContentLoader contentLoader;
    private final SchemaRegistryClient schemaRegistryClient;

    @Value("classpath:json/user-data_schema.json")
    private Resource jsonSchemaFile;

    public UserDataSchemaRegistrar(ContentLoader contentLoader, SchemaRegistryClient schemaRegistryClient) {
        this.contentLoader = contentLoader;
        this.schemaRegistryClient = schemaRegistryClient;
    }

    @PostConstruct
    public void registerUserDataSubjectJsonSchema() {
        final var subject = USER_DATA_TOPIC_NAME + "-value";

        try {
            var jsonSchemaString = contentLoader.getStringFromResource(jsonSchemaFile);
            var jsonSchema = new JsonSchema(jsonSchemaString);
            schemaRegistryClient.register(subject, jsonSchema);
        } catch (ContentLoadException | RestClientException | IOException e) {
            log.error(String.format("Schema could not be registered for subject [%s]", subject), e.getMessage(), e);
        }
    }
}
