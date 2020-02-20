package com.ractoc.eve.domain.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ractoc.eve.domain.assets.TypeModel;

import java.io.IOException;

public class TypeDeserializer extends StdDeserializer<TypeModel> {

    public static final String TYPE_ID = "id";
    public static final String NAME = "name";
    public static final String EN = "en";
    public static final String GROUP_ID = "groupID";
    public static final String GROUP_ID_V2 = "groupId";
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
    public TypeModel deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode typeNode = jp.getCodec().readTree(jp);
        TypeModel type = new TypeModel();
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
        if (typeNode.get(GROUP_ID) != null) {
            return typeNode.get(GROUP_ID).intValue();
        }
        return typeNode.get(GROUP_ID_V2).intValue();
    }

    private String getName(JsonNode typeNode) {
        String name = null;
        if (typeNode.get(NAME) != null) {
            if (typeNode.get(NAME).isTextual()) {
                name = typeNode.get(NAME).textValue();
            } else if (typeNode.get(NAME).get(EN) != null) {
                name = typeNode.get(NAME).get(EN).textValue();
            }
        }
        return name;
    }
}
