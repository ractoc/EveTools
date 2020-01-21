package com.ractoc.eve.domain.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ractoc.eve.domain.assets.TypeModel;

import java.io.IOException;

public class TypeDeserializer extends StdDeserializer<TypeModel> {

    public static final String TYPE_ID = "typeID";
    public static final String NAME = "name";
    public static final String EN = "en";
    public static final String GROUP_ID = "groupID";
    public static final String VOLUME = "volume";


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
        if (typeNode.get("published").booleanValue()) {
            TypeModel type = new TypeModel();
            type.setName(typeNode.get(NAME).get(EN).textValue());
            type.setGroupId(typeNode.get(GROUP_ID).intValue());
            type.setVolume(typeNode.get(VOLUME) != null ? typeNode.get(VOLUME).intValue() : 0);
            return type;
        }
        return null;
    }
}
