package com.bevans.kafka.connect.springboot.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaProducer {
    private final KafkaTemplate<String, GenericRecord> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, GenericRecord> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, GenericRecord>> send(String topic, String key, GenericData.Record avroRecord) {
        var producerRecord = new ProducerRecord<String, GenericRecord>(topic, key, avroRecord);
        log.info("Sending avro record {} to Kafka", producerRecord);

        return kafkaTemplate.send(producerRecord);
    }
}
