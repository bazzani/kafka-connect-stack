package com.bevans.kafka;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.json.JSONArray;

import java.util.List;

public class ArrayToJsonTransform extends BaseToJsonTransform {
    private static final String PURPOSE = "converting Connect String Array to JSON String Array";

    interface ConfigName {
        String ARRAY_FIELD_CONFIG = "arrayField";
    }

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(ConfigName.ARRAY_FIELD_CONFIG, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.CompositeValidator.of(
                            new ConfigDef.NonNullValidator(),
                            new ConfigDef.NonEmptyString()
                    ),
                    ConfigDef.Importance.HIGH,
                    "The field in the record with the String array"
            );

    private String arrayFieldName;

    @Override
    protected String purpose() {
        return PURPOSE;
    }

    @Override
    protected String fieldName() {
        return arrayFieldName;
    }

    @Override
    protected Struct makeUpdatedValue(Struct value, Schema updatedSchema) {
        var updatedValue = new Struct(updatedSchema);

        value.schema().fields().stream()
                .filter(field -> !field.name().equals(arrayFieldName))
                .forEach(field -> updatedValue.put(field.name(), value.get(field.name())));

        List<String> array = value.getArray(arrayFieldName);
        updatedValue.put(arrayFieldName, getArrayJson(array));

        return updatedValue;
    }

    private String getArrayJson(List<String> array) {
        return new JSONArray(array).toString();
    }

    @Override
    protected ConfigDef configDef() {
        return CONFIG_DEF;
    }

    @Override
    protected void getConfigValues(SimpleConfig config) {
        arrayFieldName = config.getString(ConfigName.ARRAY_FIELD_CONFIG);
    }
}
