package com.bevans.kafka.connect.transforms;

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
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.bevans.kafka.connect.transforms.ArrayToJsonTransform.ConfigName.ARRAY_FIELD_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArrayToJsonTransformTest {
    private static final String ARRAY_FIELD_NAME = "fieldWithArray";
    private static final String INT_FIELD_NAME = "intField";

    private ArrayToJsonTransform sut;

    @BeforeEach
    public void setup() {
        sut = new ArrayToJsonTransform();
        sut.configure(Map.of(ArrayToJsonTransform.ConfigName.ARRAY_FIELD_CONFIG, ARRAY_FIELD_NAME, "foo", true));
    }

    @Test
    void shouldApplyAndTransformToJsonArray() {
        // given
        var valueSchema = SchemaBuilder.struct()
                .field(ARRAY_FIELD_NAME, SchemaBuilder.array(Schema.STRING_SCHEMA))
                .field(INT_FIELD_NAME, Schema.INT32_SCHEMA)
                .build();

        var value = new Struct(valueSchema);
        value.put(ARRAY_FIELD_NAME, List.of("Earth", "Wind", "Fire"));
        value.put(INT_FIELD_NAME, 123);

        var sinkRecord = new SinkRecord("topic", 1, Schema.STRING_SCHEMA, "key", valueSchema, value, 0);

        // when
        var transformedRecord = sut.apply(sinkRecord);

        // then
        var transformedValue = (Struct) transformedRecord.value();
        assertThat(transformedValue.schema().field(ARRAY_FIELD_NAME).schema().type()).isEqualTo(Schema.Type.STRING);
        assertThat(transformedValue.getString(ARRAY_FIELD_NAME)).isNotNull();
        assertThat(transformedValue.getString(ARRAY_FIELD_NAME)).isEqualTo("[\"Earth\",\"Wind\",\"Fire\"]");

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
                .containsKey(ARRAY_FIELD_CONFIG);
        assertThat(configKeys.get(ARRAY_FIELD_CONFIG).type).isEqualTo(ConfigDef.Type.STRING);
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
        assertThatNoException().isThrownBy(() -> sut.configure(Map.of(ARRAY_FIELD_CONFIG, "foo")));
    }

    @ParameterizedTest
    @MethodSource("badConfigData")
    void shouldFailConfigure(Map<String, ?> configs, String expectedError) {
        // given
        sut = new ArrayToJsonTransform();

        var exception = assertThrows(ConfigException.class, () -> {
            // when
            sut.configure(configs);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo(expectedError);
    }

    private static Stream<Arguments> badConfigData() {
        var nullValueMap = new HashMap<>();
        nullValueMap.put(ArrayToJsonTransform.ConfigName.ARRAY_FIELD_CONFIG, null);

        return Stream.of(
                Arguments.of(Map.of("otherField", ARRAY_FIELD_NAME),
                        "Missing required configuration \"arrayField\" which has no default value."),
                Arguments.of(nullValueMap,
                        "Invalid value null for configuration arrayField: entry must be non null"),
                Arguments.of(Map.of(ArrayToJsonTransform.ConfigName.ARRAY_FIELD_CONFIG, ""),
                        "Invalid value  for configuration arrayField: String must be non-empty")
        );
    }
}
