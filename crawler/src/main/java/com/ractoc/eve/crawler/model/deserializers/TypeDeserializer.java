package com.ractoc.eve.crawler.model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ractoc.eve.crawler.model.YamlTypeModel;

import java.io.IOException;

public class TypeDeserializer extends StdDeserializer<YamlTypeModel> {

    public static final String TYPE_ID = "id";
    public static final String NAME = "name";
    public static final String EN = "en";
    public static final String GROUP_ID = "groupID";
    public static final String VOLUME = "volume";
    public static final String PUBLISHED = "published";


    @SuppressWarnings("unused")
    public TypeDeserializer() {
        this(null);
    }

    public TypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public YamlTypeModel deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode typeNode = jp.getCodec().readTree(jp);
        YamlTypeModel type = new YamlTypeModel();
        try {
            type.setId(getId(typeNode));
            type.setName(getName(typeNode));
            type.setGroupId(getGroupId(typeNode));
            type.setVolume(getVolume(typeNode));
            type.setPublished(typeNode.get(PUBLISHED).booleanValue());
            return type;
        } catch (Exception e) {
            System.out.println("unable to process type: " + type);
            throw e;
        }
    }

    private double getVolume(JsonNode typeNode) {
        return typeNode.get(VOLUME) != null ? typeNode.get(VOLUME).doubleValue() : 0.0;
    }

    private int getId(JsonNode typeNode) {
        return typeNode.get(TYPE_ID) != null ? typeNode.get(TYPE_ID).intValue() : 0;
    }

    private int getGroupId(JsonNode typeNode) {
            return typeNode.get(GROUP_ID).intValue();
    }

    private String getName(JsonNode typeNode) {
        if (typeNode.get(NAME).get(EN) != null) {
            return typeNode.get(NAME).get(EN).textValue();
        }
        return null;
    }
}
