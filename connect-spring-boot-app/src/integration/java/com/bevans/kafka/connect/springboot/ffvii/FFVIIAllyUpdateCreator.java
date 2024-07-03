package com.bevans.kafka.connect.springboot.ffvii;

import com.bevans.avro.ffvii.FFViiAllyUpdate;
import com.bevans.avro.ffvii.weapon.FFViiAllyWeapon;
import com.bevans.kafka.connect.springboot.ffvii.exception.FFVIIException;
import com.bevans.kafka.connect.springboot.content.ContentLoadException;
import com.bevans.kafka.connect.springboot.content.ContentLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class FFVIIAllyUpdateCreator {
    @Value("classpath:avro/ff_vii.ally_updates.v1-value.avsc")
    private Resource avroSchemaFile;
    @Value("classpath:avro-data/cloud-strife-update.json")
    private Resource avroDataFile;

    private final ContentLoader contentLoader;
    private final ObjectMapper mapper;
    private final Schema.Parser parser;

    public FFVIIAllyUpdateCreator(ContentLoader contentLoader) {
        this.contentLoader = contentLoader;
        this.mapper = new ObjectMapper();
        this.parser = new Schema.Parser();
    }

    public GenericData.Record createAvroRecord(String name) {
        try {
            var avroSchema = getAvroSchema();
            var avroData = getAvroData();

            return createAvroRecord(name, avroSchema, avroData);
        } catch (Exception e) {
            throw new FFVIIException("Unable to process AVRO Schema or Data", e);
        }
    }

    public FFViiAllyUpdate getAvroData() {
        try {
            var avroDataString = contentLoader.getStringFromResource(avroDataFile);
            return mapper.readValue(avroDataString, FFViiAllyUpdate.class);
        } catch (ContentLoadException | JsonProcessingException e) {
            throw new FFVIIException("Error processing Avro Data Json", e);
        }
    }

    private Schema getAvroSchema() {
        try {
            var avroSchemaString = contentLoader.getStringFromResource(avroSchemaFile);
            return parser.parse(avroSchemaString);
        } catch (ContentLoadException e) {
            throw new FFVIIException("Error loading Avro schema", e);
        }
    }

    private GenericData.Record createAvroRecord(String name, Schema schema, FFViiAllyUpdate ffViiAllyUpdate) {
        var avroRecord = new GenericData.Record(schema);

        var weaponRecord = getWeaponRecord(schema, ffViiAllyUpdate.getWeapon());
        avroRecord.put("weapon", weaponRecord);

        avroRecord.put("name", ffViiAllyUpdate.getName() + "_" + name);
        avroRecord.put("age", ffViiAllyUpdate.getAge());
        avroRecord.put("exp", ffViiAllyUpdate.getExp());
        avroRecord.put("hp", ffViiAllyUpdate.getHp());
        avroRecord.put("mp", ffViiAllyUpdate.getMp());
        avroRecord.put("winLossRatio", ffViiAllyUpdate.getWinLossRatio());
        avroRecord.put("psStoreCost", ffViiAllyUpdate.getPsStoreCost());
        avroRecord.put("nameInBytes", ffViiAllyUpdate.getNameInBytes());
        avroRecord.put("materia", ffViiAllyUpdate.getMateria());

        return avroRecord;
    }

    private GenericData.Record getWeaponRecord(Schema schema, FFViiAllyWeapon weapon) {
        var weaponRecord = new GenericData.Record(schema.getField("weapon").schema());

        weaponRecord.put("name", weapon.getName());
        weaponRecord.put("attack", weapon.getAttack());
        weaponRecord.put("magicAttack", weapon.getMagicAttack());

        return weaponRecord;
    }
}
