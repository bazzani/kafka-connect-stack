package com.bevans.kafka.connect.transforms;

import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.cache.LRUCache;
import org.apache.kafka.common.cache.SynchronizedCache;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SchemaUtil;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import java.util.Map;

import static org.apache.kafka.connect.transforms.util.Requirements.requireStruct;

public abstract class BaseToJsonTransform implements Transformation<SinkRecord> {
    private Cache<Schema, Schema> schemaUpdateCache;

    @Override
    public SinkRecord apply(SinkRecord record) {
        return applyWithSchema(record);
    }

    private SinkRecord applyWithSchema(SinkRecord record) {
        var value = requireStruct(record.value(), purpose());
        var updatedSchema = getUpdatedSchema(value.schema());
        var updatedValue = makeUpdatedValue(value, updatedSchema);

        return newRecord(record, updatedSchema, updatedValue);
    }

    protected abstract String purpose();

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
                .filter(field -> !field.name().equals(fieldName()))
                .forEach(field -> builder.field(field.name(), field.schema()));

        builder.field(fieldName(), Schema.STRING_SCHEMA);  // todo also copy the original schema props like optional?

        return builder.build();
    }

    protected abstract String fieldName();

    protected abstract Struct makeUpdatedValue(Struct value, Schema updatedSchema);

    private SinkRecord newRecord(SinkRecord oldRecord, Schema updatedSchema, Struct updatedValue) {
        return oldRecord.newRecord(oldRecord.topic(), oldRecord.kafkaPartition(),
                oldRecord.keySchema(), oldRecord.key(),
                updatedSchema, updatedValue, oldRecord.timestamp());
    }

    @Override
    public ConfigDef config() {
        return configDef();
    }

    protected abstract ConfigDef configDef();

    @Override
    public void configure(Map<String, ?> configs) {
        final SimpleConfig config = new SimpleConfig(configDef(), configs);
        getConfigValues(config);
        schemaUpdateCache = new SynchronizedCache<>(new LRUCache<>(16));
    }

    protected abstract void getConfigValues(SimpleConfig config);

    @Override
    public void close() {
        schemaUpdateCache = null;
    }
}
