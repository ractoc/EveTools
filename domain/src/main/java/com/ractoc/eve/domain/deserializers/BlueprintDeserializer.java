package com.ractoc.eve.domain.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ractoc.eve.domain.assets.BlueprintMaterialModel;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.domain.assets.BlueprintProductModel;
import com.ractoc.eve.domain.assets.BlueprintSkillModel;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class BlueprintDeserializer extends StdDeserializer<BlueprintModel> {

    public static final String TYPE_ID = "typeID";
    public static final String QUANTITY = "quantity";
    public static final String MATERIALS = "materials";
    public static final String BLUEPRINT_TYPE_ID = "blueprintTypeID";
    public static final String MAX_PRODUCTION_LIMIT = "maxProductionLimit";
    public static final String ACTIVITIES = "activities";
    public static final String COPYING = "copying";
    public static final String INVENTION = "invention";
    public static final String TIME = "time";
    public static final String MANUFACTURING = "manufacturing";
    public static final String RESEARCH_MATERIAL = "research_material";
    public static final String RESEARCH_TIME = "research_time";
    public static final String PRODUCTS = "products";
    public static final String PROBABILITY = "probability";
    public static final String SKILLS = "skills";
    public static final String LEVEL = "level";
    public static final String ID = "id";


    @SuppressWarnings("unused")
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
        bp.setId(getBlueprintId(bpNode));
        bp.setName(bpNode.get("name").toString());
        bp.setLocationId(bpNode.get("locationId").longValue());
        bp.setMaxProductionLimit(bpNode.get(MAX_PRODUCTION_LIMIT).intValue());
        bp.setMaterialEfficiency(bpNode.get("materialEfficiency") != null ? bpNode.get("materialEfficiency").intValue() : 0);
        bp.setTimeEfficiency(bpNode.get("timeEfficiency") != null ? bpNode.get("timeEfficiency").intValue() : 0);
        deserializeActivities(bp, bpNode);
        return bp;
    }

    private int getBlueprintId(JsonNode bpNode) {
        JsonNode idNode = bpNode.get(BLUEPRINT_TYPE_ID);
        if (idNode == null) {
            idNode = bpNode.get(ID);
        }
        return idNode.asInt();
    }

    private void deserializeActivities(BlueprintModel bp, JsonNode bpNode) {
        Optional<JsonNode> actOpt = getChildNode(bpNode, ACTIVITIES);
        if (actOpt.isPresent()) {
            JsonNode actNode = actOpt.get();
            deserializeCopying(bp, actNode);
            deserializeInvention(bp, actNode);
            deserializeManufacturing(bp, actNode);
            deserializeResearchMaterialTime(bp, actNode);
            deserializeResearchTimeTime(bp, actNode);
        }
    }

    private void deserializeCopying(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, COPYING);
        opt.ifPresent(node -> bp.setCopyingTime(node.get(TIME).intValue()));
    }

    private void deserializeInvention(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, INVENTION);
        if (opt.isPresent()) {
            JsonNode node = opt.get();
            bp.setInventionTime(node.get(TIME).intValue());
            deserializeMaterials(bp, node, bp.getInventionMaterials());
            deserializeProducts(bp, node, bp.getInventionProducts());
            deserializeSkills(bp, node, bp.getInventionSkills());
        }
    }

    private void deserializeManufacturing(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, MANUFACTURING);
        if (opt.isPresent()) {
            JsonNode node = opt.get();
            bp.setManufacturingTime(node.get(TIME).intValue());
            deserializeMaterials(bp, node, bp.getManufacturingMaterials());
            deserializeProducts(bp, node, bp.getManufacturingProducts());
            deserializeSkills(bp, node, bp.getManufacturingSkills());
        }
    }

    private void deserializeResearchMaterialTime(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, RESEARCH_MATERIAL);
        opt.ifPresent(node -> bp.setResearchMaterialTime(node.get(TIME).intValue()));
    }

    private void deserializeResearchTimeTime(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, RESEARCH_TIME);
        opt.ifPresent(node -> bp.setResearchTimeTime(node.get(TIME).intValue()));
    }

    private Optional<JsonNode> getChildNode(JsonNode node, String child) {
        return Optional.ofNullable(node.get(child));
    }

    private void deserializeMaterials(BlueprintModel bp, JsonNode node, Set<BlueprintMaterialModel> mats) {
        Optional<JsonNode> matOpt = getChildNode(node, MATERIALS);
        if (matOpt.isPresent()) {
            for (JsonNode matNode : matOpt.get()) {
                BlueprintMaterialModel.BlueprintMaterialModelBuilder builder = BlueprintMaterialModel.builder();
                builder.blueprintId(bp.getId());
                builder.typeId(matNode.get(TYPE_ID).intValue());
                builder.quantity(matNode.get(QUANTITY).intValue());
                BlueprintMaterialModel bpMat = builder
                        .build();
                mats.add(bpMat);
            }
        }
    }

    private void deserializeProducts(BlueprintModel bp, JsonNode node, Set<BlueprintProductModel> prods) {
        Optional<JsonNode> prodOpt = getChildNode(node, PRODUCTS);
        if (prodOpt.isPresent()) {
            for (JsonNode prodNode : prodOpt.get()) {
                BlueprintProductModel bpProd = BlueprintProductModel.builder()
                        .blueprintId(bp.getId())
                        .typeId(prodNode.get(TYPE_ID).intValue())
                        .probability(prodNode.get(PROBABILITY) != null ? prodNode.get(PROBABILITY).floatValue() : 0.0f)
                        .quantity(prodNode.get(QUANTITY).intValue())
                        .build();
                prods.add(bpProd);
            }
        }
    }

    private void deserializeSkills(BlueprintModel bp, JsonNode node, Set<BlueprintSkillModel> skills) {
        Optional<JsonNode> skillOpt = getChildNode(node, SKILLS);
        if (skillOpt.isPresent()) {
            for (JsonNode skillNode : skillOpt.get()) {
                BlueprintSkillModel bpSkill = BlueprintSkillModel.builder()
                        .blueprintId(bp.getId())
                        .typeId(skillNode.get(TYPE_ID).intValue())
                        .level(skillNode.get(LEVEL).intValue())
                        .build();
                skills.add(bpSkill);
            }
        }
    }
}
