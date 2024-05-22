package com.bevans.kafka.connect.transforms;

import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.cache.LRUCache;
import org.apache.kafka.common.cache.SynchronizedCache;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SchemaUtil;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import java.time.Instant;
import java.util.Map;

import static org.apache.kafka.connect.transforms.util.Requirements.requireStruct;

public class InsertNowFieldTransform implements Transformation<SinkRecord> {
    private static final String PURPOSE = "insert a field with a timestamp of now()";

    interface ConfigName {
        String FIELD_CONFIG = "field";
    }

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(ConfigName.FIELD_CONFIG, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.CompositeValidator.of(
                            new ConfigDef.NonNullValidator(),
                            new ConfigDef.NonEmptyString()
                    ),
                    ConfigDef.Importance.HIGH,
                    "The field in the record to add the timestamp to"
            );

    private Cache<Schema, Schema> schemaUpdateCache;
    private String fieldName;

    @Override
    public SinkRecord apply(SinkRecord record) {
        return applyWithSchema(record);
    }

    private SinkRecord applyWithSchema(SinkRecord record) {
        var value = requireStruct(record.value(), PURPOSE);
        var updatedSchema = getUpdatedSchema(value.schema());
        var updatedValue = makeUpdatedValue(value, updatedSchema);

        return newRecord(record, updatedSchema, updatedValue);
    }

    private Schema getUpdatedSchema(Schema originalSchema) {
        var updatedSchema = schemaUpdateCache.get(originalSchema);

        if (updatedSchema == null) {
            updatedSchema = makeUpdatedSchema(originalSchema);
            schemaUpdateCache.put(originalSchema, updatedSchema);
        }

        return updatedSchema;
    }

    private Schema makeUpdatedSchema(Schema schema) {
        var builder = SchemaUtil.copySchemaBasics(schema, SchemaBuilder.struct());

        schema.fields().stream()
                .filter(field -> !field.name().equals(fieldName))
                .forEach(field -> builder.field(field.name(), field.schema()));

        builder.field(fieldName, Timestamp.SCHEMA); // todo also copy the original schema props like optional?

        return builder.build();
    }

    private Struct makeUpdatedValue(Struct value, Schema updatedSchema) {
        var updatedValue = new Struct(updatedSchema);

        updatedSchema.fields()
                .stream()
                .filter(field -> !field.name().equals(fieldName))
                .forEach(field -> updatedValue.put(field.name(), value.get(field.name())));

        var now = java.util.Date.from(Instant.now());
        updatedValue.put(fieldName, now);

        return updatedValue;
    }

    private SinkRecord newRecord(SinkRecord oldRecord, Schema updatedSchema, Struct updatedValue) {
        return oldRecord.newRecord(oldRecord.topic(), oldRecord.kafkaPartition(),
                oldRecord.keySchema(), oldRecord.key(),
                updatedSchema, updatedValue, oldRecord.timestamp());
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void configure(Map<String, ?> configs) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, configs);
        fieldName = config.getString(ConfigName.FIELD_CONFIG);
        schemaUpdateCache = new SynchronizedCache<>(new LRUCache<>(16));
    }

    @Override
    public void close() {
        schemaUpdateCache = null;
    }
}
