package com.bevans.kafka;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StructToJsonTransformTest {
    private static final String STRUCT_FIELD_NAME = "weapon";
    private static final String INT_FIELD_NAME = "intField";

    private StructToJsonTransform sut;

    @BeforeEach
    public void setup() {
        sut = new StructToJsonTransform();
        sut.configure(Map.of(StructToJsonTransform.ConfigName.STRUCT_FIELD_CONFIG, STRUCT_FIELD_NAME));
    }

    @Test
    void shouldApplyAndTransformToJsonObject() {
        // given
        var weaponSchema = SchemaBuilder.struct()
                .field("name", Schema.STRING_SCHEMA)
                .field("attack", Schema.INT32_SCHEMA)
                .field("magic_attack", Schema.INT32_SCHEMA)
                .build();
        var valueSchema = SchemaBuilder.struct()
                .field(STRUCT_FIELD_NAME, weaponSchema)
                .field(INT_FIELD_NAME, Schema.INT32_SCHEMA)
                .build();

        var weaponStruct = new Struct(weaponSchema);
        weaponStruct.put("name", "Buster Sword");
        weaponStruct.put("attack", 22);
        weaponStruct.put("magic_attack", 22);

        var value = new Struct(valueSchema);
        value.put(STRUCT_FIELD_NAME, weaponStruct);
        value.put(INT_FIELD_NAME, 123);

        var sinkRecord = new SinkRecord("topic", 1, Schema.STRING_SCHEMA, "key", valueSchema, value, 0);

        // when
        var transformedRecord = sut.apply(sinkRecord);

        // then
        var transformedValue = (Struct) transformedRecord.value();
        assertThat(transformedValue.schema().field(STRUCT_FIELD_NAME).schema().type()).isEqualTo(Schema.Type.STRING);
        assertThat(transformedValue.getString(STRUCT_FIELD_NAME)).isNotNull();
        assertThat(transformedValue.getString(STRUCT_FIELD_NAME)).isEqualTo("{\"attack\":22,\"magic_attack\":22,\"name\":\"Buster Sword\"}");

        assertThat(transformedValue.schema().field(INT_FIELD_NAME).schema().type()).isEqualTo(Schema.Type.INT32);
        assertThat(transformedValue.getInt32(INT_FIELD_NAME)).isNotNull();
        assertThat(transformedValue.getInt32(INT_FIELD_NAME)).isEqualTo(123);

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
                .containsKey(StructToJsonTransform.ConfigName.STRUCT_FIELD_CONFIG);
        assertThat(configKeys.get(StructToJsonTransform.ConfigName.STRUCT_FIELD_CONFIG).type).isEqualTo(ConfigDef.Type.STRING);
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
        assertThatNoException().isThrownBy(() -> sut.configure(Map.of(StructToJsonTransform.ConfigName.STRUCT_FIELD_CONFIG, "foo")));
    }

    @ParameterizedTest
    @MethodSource("badConfigData")
    void shouldFailConfigure(Map<String, ?> configs, String expectedError) {
        // given
        sut = new StructToJsonTransform();

        var exception = assertThrows(ConfigException.class, () -> {
            // when
            sut.configure(configs);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo(expectedError);
    }

    private static Stream<Arguments> badConfigData() {
        var nullValueMap = new HashMap<>();
        nullValueMap.put(StructToJsonTransform.ConfigName.STRUCT_FIELD_CONFIG, null);

        return Stream.of(
                Arguments.of(Map.of("otherField", STRUCT_FIELD_NAME),
                        "Missing required configuration \"structField\" which has no default value."),
                Arguments.of(nullValueMap,
                        "Invalid value null for configuration structField: entry must be non null"),
                Arguments.of(Map.of(StructToJsonTransform.ConfigName.STRUCT_FIELD_CONFIG, ""),
                        "Invalid value  for configuration structField: String must be non-empty")
        );
    }
}
