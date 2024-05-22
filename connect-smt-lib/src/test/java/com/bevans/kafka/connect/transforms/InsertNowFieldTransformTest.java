package com.bevans.kafka.connect.transforms;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InsertNowFieldTransformTest {
    private static final String FIELD_NAME = "nowField";
    private static final String INT_FIELD_NAME = "intField";

    private InsertNowFieldTransform sut;

    @BeforeEach
    public void setup() {
        sut = new InsertNowFieldTransform();
        sut.configure(Map.of(InsertNowFieldTransform.ConfigName.FIELD_CONFIG, FIELD_NAME));
    }

    @Test
    void shouldAddNowTimestampField() {
        // given
        var valueSchema = SchemaBuilder.struct()
                .field(INT_FIELD_NAME, Schema.INT32_SCHEMA)
                .build();

        var value = new Struct(valueSchema);
        value.put(INT_FIELD_NAME, 123);

        var sinkRecord = new SinkRecord("topic", 1, Schema.STRING_SCHEMA, "key", valueSchema, value, 0);

        // when
        var transformedRecord = sut.apply(sinkRecord);

        // then
        var transformedValue = (Struct) transformedRecord.value();

        var schema = transformedValue.schema().field(FIELD_NAME).schema();
        assertThat(schema.type()).isEqualTo(Schema.Type.INT64);
        assertThat(schema.name()).isEqualTo(Timestamp.LOGICAL_NAME);

        var now = java.util.Date.from(Instant.now());
        var insertedDate = (Date) transformedValue.get(FIELD_NAME);
        assertThat(now).isInSameSecondWindowAs(insertedDate);

        assertThat(transformedValue.schema().field(INT_FIELD_NAME).schema().type()).isEqualTo(Schema.Type.INT32);
        assertThat(transformedValue.getInt32(INT_FIELD_NAME)).isNotNull();
        assertThat(transformedValue.getInt32(INT_FIELD_NAME)).isEqualTo(123);

        // call second time to get full coverage on schema caching // todo move the schema caching to a new class
        sut.apply(sinkRecord);
    }

    @Test
    void shouldAddNowTimestampFieldWhenFieldAlreadyExists() {
        // given
        var valueSchema = SchemaBuilder.struct()
                .field(FIELD_NAME, Timestamp.SCHEMA)
                .build();

        var value = new Struct(valueSchema);
        value.put(FIELD_NAME, Date.from(LocalDateTime.of(2025, 1, 1, 1, 1, 1).toInstant(ZoneOffset.UTC)));

        var sinkRecord = new SinkRecord("topic", 1, Schema.STRING_SCHEMA, "key", valueSchema, value, 0);

        // when
        var transformedRecord = sut.apply(sinkRecord);

        // then
        var transformedValue = (Struct) transformedRecord.value();

        var schema = transformedValue.schema().field(FIELD_NAME).schema();
        assertThat(schema.type()).isEqualTo(Schema.Type.INT64);
        assertThat(schema.name()).isEqualTo(Timestamp.LOGICAL_NAME);

        var now = java.util.Date.from(Instant.now());
        var insertedDate = (Date) transformedValue.get(FIELD_NAME);
        assertThat(now).isInSameSecondWindowAs(insertedDate);

        // call second time to get full coverage on schema caching // todo move the schema caching to a new class
        sut.apply(sinkRecord);
    }

    @Test
    void shouldGetConfig() {
        // given
        // when
        var configDef = sut.config();

        // then
        var configKeys = configDef.configKeys();
        assertThat(configKeys)
                .hasSize(1)
                .containsKey(InsertNowFieldTransform.ConfigName.FIELD_CONFIG);
        assertThat(configKeys.get(InsertNowFieldTransform.ConfigName.FIELD_CONFIG).type).isEqualTo(ConfigDef.Type.STRING);
    }

    @Test
    void shouldClose() {
        // given
        // when
        // then
        assertThatNoException().isThrownBy(() -> sut.close());
    }

    @Test
    void shouldConfigure() {
        // given
        // when
        // then
        assertThatNoException().isThrownBy(() -> sut.configure(Map.of(InsertNowFieldTransform.ConfigName.FIELD_CONFIG, "foo")));
    }

    @ParameterizedTest
    @MethodSource("badConfigData")
    void shouldFailConfigure(Map<String, ?> configs, String expectedError) {
        // given
        sut = new InsertNowFieldTransform();

        var exception = assertThrows(ConfigException.class, () -> {
            // when
            sut.configure(configs);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo(expectedError);
    }

    private static Stream<Arguments> badConfigData() {
        var nullValueMap = new HashMap<>();
        nullValueMap.put(InsertNowFieldTransform.ConfigName.FIELD_CONFIG, null);

        return Stream.of(
                Arguments.of(Map.of("otherField", FIELD_NAME),
                        "Missing required configuration \"field\" which has no default value."),
                Arguments.of(nullValueMap,
                        "Invalid value null for configuration field: entry must be non null"),
                Arguments.of(Map.of(InsertNowFieldTransform.ConfigName.FIELD_CONFIG, ""),
                        "Invalid value  for configuration field: String must be non-empty")
        );
    }
}
