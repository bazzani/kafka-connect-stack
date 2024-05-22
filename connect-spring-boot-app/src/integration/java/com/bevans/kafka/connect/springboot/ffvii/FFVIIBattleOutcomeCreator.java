package com.bevans.kafka.connect.springboot.ffvii;

import com.bevans.avro.ffvii.FFViiBattleOutcome;
import com.bevans.kafka.connect.springboot.ffvii.exception.FFVIIException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FFVIIBattleOutcomeCreator {
    @Value("classpath:avro/ff_vii.battle_outcomes.v1-value.avsc")
    private Resource avroSchemaFile;
    @Value("classpath:avro-data/cloud-tifa-battle-outcome.json")
    private Resource avroDataFile;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Schema.Parser parser = new Schema.Parser();

    public GenericData.Record createAvroRecord() {
        try {
            var avroSchema = getAvroSchema();
            var avroData = getAvroData();

            return createAvroRecord(avroSchema, avroData);
        } catch (Exception e) {
            throw new FFVIIException("Unable to process AVRO Schema or Data", e);
        }
    }

    public FFViiBattleOutcome getAvroData() {
        var avroDataString = getStringFromResource(avroDataFile);

        try {
            return mapper.readValue(avroDataString, FFViiBattleOutcome.class);
        } catch (JsonProcessingException e) {
            throw new FFVIIException("Error processing Avro Data Json", e);
        }
    }

    private Schema getAvroSchema() {
        var avroSchemaString = getStringFromResource(avroSchemaFile);
        return parser.parse(avroSchemaString);
    }

    private String getStringFromResource(Resource resource) {
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FFVIIException("Unable to locate resource", e);
        }
    }

    private GenericData.Record createAvroRecord(Schema schema, FFViiBattleOutcome ffviiBattleOutcome) {
        var avroRecord = new GenericData.Record(schema);

        avroRecord.put("winOrLoss", ffviiBattleOutcome.getWinOrLoss());
        avroRecord.put("allies", ffviiBattleOutcome.getAllies());
        avroRecord.put("expEarned", ffviiBattleOutcome.getExpEarned());
        avroRecord.put("apEarned", ffviiBattleOutcome.getApEarned());

        return avroRecord;
    }
}
