package com.ractoc.eve.domain.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ractoc.eve.domain.assets.BlueprintMaterialModel;
import com.ractoc.eve.domain.assets.BlueprintModel;

import java.io.IOException;
import java.util.Optional;

public class BlueprintDeserializer extends StdDeserializer<BlueprintModel> {

    public BlueprintDeserializer() {
        this(null);
    }

    public BlueprintDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public BlueprintModel deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode bpNode = jp.getCodec().readTree(jp);
        BlueprintModel bp = new BlueprintModel();
        bp.setId(bpNode.get("blueprintTypeID").intValue());
        bp.setMaxProductionLimit(bpNode.get("maxProductionLimit").intValue());
        bp = deserializeActivities(bp, bpNode);
        return bp;
    }

    private BlueprintModel deserializeActivities(BlueprintModel bp, JsonNode bpNode) {
        Optional<JsonNode> actOpt = getChildNode(bpNode, "activities");
        if (actOpt.isPresent()) {
            JsonNode actNode = actOpt.get();
            deserializeCopying(bp, actNode);
            deserializeInvention(bp, actNode);
            deserializeManufacturing(bp, actNode);
            deserializeResearchMaterial(bp, actNode);
            deserializeResearchTime(bp, actNode);
        }
        return bp;
    }

    private void deserializeCopying(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, "copying");
        if (opt.isPresent()) {
            bp.setCopyingTime(opt.get().get("time").intValue());
        }
    }

    private void deserializeInvention(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, "invention");
        if (opt.isPresent()) {
            bp.setInventionTime(opt.get().get("time").intValue());
            Optional<JsonNode> matOpt =  getChildNode(opt.get(), "materials");
            if (matOpt.isPresent()) {
                for (JsonNode matNode : matOpt.get()) {
                    BlueprintMaterialModel bpMat = BlueprintMaterialModel.builder()
                            .blueprintId(bp.getId())
                            .typeId(matNode.get("typeID").intValue())
                            .quantity(matNode.get("quantity").intValue())
                            .build();
                    bp.getInventionMaterials().add(bpMat);
                }
            }
        }
    }

    private void deserializeManufacturing(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, "manufacturing");
        if (opt.isPresent()) {
            bp.setManufacturingTime(opt.get().get("time").intValue());
        }
    }

    private void deserializeResearchMaterial(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, "research_material");
        if (opt.isPresent()) {
            bp.setResearchMaterialTime(opt.get().get("time").intValue());
        }
    }

    private void deserializeResearchTime(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, "research_time");
        if (opt.isPresent()) {
            bp.setResearchTimeTime(opt.get().get("time").intValue());
        }
    }

    private Optional<JsonNode> getChildNode(JsonNode node, String child) {
        return Optional.ofNullable(node.get(child));
    }
}
