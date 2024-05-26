package com.bevans.kafka.connect.transforms;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.json.JSONObject;

public class StructToJsonTransform extends BaseToJsonTransform {
    private static final String PURPOSE = "convert Connect Struct to JSON String Object";

    interface ConfigName {
        String STRUCT_FIELD_CONFIG = "structField";
    }

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(ConfigName.STRUCT_FIELD_CONFIG, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.CompositeValidator.of(
                            new ConfigDef.NonNullValidator(),
                            new ConfigDef.NonEmptyString()
                    ),
                    ConfigDef.Importance.HIGH,
                    "The field in the record with the Struct"
            );

    private String structFieldName;

    @Override
    protected String purpose() {
        return PURPOSE;
    }

    @Override
    protected String fieldName() {
        return structFieldName;
    }

    @Override
    protected Struct makeUpdatedValue(Struct value, Schema updatedSchema) {
        var updatedValue = new Struct(updatedSchema);

        updatedSchema.fields()
                .stream()
                .filter(field -> !field.name().equals(fieldName()))
                .forEach(field -> updatedValue.put(field.name(), value.get(field.name())));

        Struct struct = value.getStruct(fieldName());
        updatedValue.put(fieldName(), getStructJson(struct));

        if (structFieldName.equals("barry")) {
            for (int i = 0; i < 100; i++) {
                var s = 4;
            }
        }

        return updatedValue;
    }

    private String getStructJson(Struct struct) {
        var jsonObject = new JSONObject();

        struct.schema()
                .fields()
                .forEach(field -> jsonObject.put(field.name(), struct.get(field.name())));

        return jsonObject.toString();
    }

    @Override
    protected ConfigDef configDef() {
        return CONFIG_DEF;
    }

    @Override
    protected void getConfigValues(SimpleConfig config) {
        structFieldName = config.getString(ConfigName.STRUCT_FIELD_CONFIG);
    }
}
