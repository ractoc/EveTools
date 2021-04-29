package com.ractoc.eve.crawler.model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ractoc.eve.crawler.model.YamlMarketGroupModel;

import java.io.IOException;

public class MarketGroupDeserializer extends StdDeserializer<YamlMarketGroupModel> {

    public static final String MarketGroup_ID = "id";
    public static final String NAME = "nameID";
    public static final String EN = "en";
    public static final String PARENT_GROUP_ID = "parentGroupID";


    @SuppressWarnings("unused")
    public MarketGroupDeserializer() {
        this(null);
    }

    public MarketGroupDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public YamlMarketGroupModel deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode marketGroupNode = jp.getCodec().readTree(jp);
        YamlMarketGroupModel marketGroup = new YamlMarketGroupModel();
        try {
            marketGroup.setId(getId(marketGroupNode));
            marketGroup.setName(getName(marketGroupNode));
            marketGroup.setParentGroupId(getParentGroupId(marketGroupNode));
            return marketGroup;
        } catch (Exception e) {
            System.out.println("unable to process marketGroup: " + marketGroup);
            throw e;
        }
    }

    private int getId(JsonNode marketGroupNode) {
        return marketGroupNode.get(MarketGroup_ID) != null ? marketGroupNode.get(MarketGroup_ID).intValue() : 0;
    }

    private int getParentGroupId(JsonNode marketGroupNode) {
        return marketGroupNode.get(PARENT_GROUP_ID) != null ? marketGroupNode.get(PARENT_GROUP_ID).intValue() : 0;
    }

    private String getName(JsonNode marketGroupNode) {
        if (marketGroupNode.get(NAME) != null && marketGroupNode.get(NAME).get(EN) != null) {
            return marketGroupNode.get(NAME).get(EN).textValue();
        }
        return null;
    }
}
