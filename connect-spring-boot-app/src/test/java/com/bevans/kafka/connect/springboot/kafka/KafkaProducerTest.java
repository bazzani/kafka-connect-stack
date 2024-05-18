package com.bevans.kafka.connect.springboot.kafka;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    private static final String TOPIC = "topic";
    private static final String KEY = "key";

    private KafkaProducer sut;

    @Mock
    private KafkaTemplate<String, GenericRecord> kafkaTemplateMock;

    @Captor
    private ArgumentCaptor<ProducerRecord<String, GenericRecord>> producerRecordCaptor;

    @BeforeEach
    void setUp() {
        sut = new KafkaProducer(kafkaTemplateMock);
    }

    @Test
    void shouldSend() {
        // given
        var avroRecord = mock(GenericData.Record.class);

        var producerRecord = new ProducerRecord<String, GenericRecord>(TOPIC, avroRecord);
        var recordMetadata = new RecordMetadata(new TopicPartition(TOPIC, 1), 0, 0, 1234L, 1, 2);
        var completedFuture = CompletableFuture.completedFuture(new SendResult<>(producerRecord, recordMetadata));

        when(kafkaTemplateMock.send(argThat((ProducerRecord<String, GenericRecord> arg) ->
                arg.topic().equals(TOPIC) &&
                        arg.key().equals(KEY) &&
                        arg.value().equals(avroRecord)
        ))).thenReturn(completedFuture);

        // when
        var result = sut.send(TOPIC, KEY, avroRecord);

        // then
        assertThat(result).isEqualTo(completedFuture);

        verify(kafkaTemplateMock).send(producerRecordCaptor.capture());
        var capturedRecord = producerRecordCaptor.getValue();
        assertThat(capturedRecord.topic()).isEqualTo(TOPIC);
        assertThat(capturedRecord.key()).isEqualTo(KEY);
        assertThat(capturedRecord.value()).isEqualTo(avroRecord);
    }
}
