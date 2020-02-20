package com.ractoc.eve.crawler.model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ractoc.eve.crawler.model.YamlBlueprintMaterialModel;
import com.ractoc.eve.crawler.model.YamlBlueprintModel;
import com.ractoc.eve.crawler.model.YamlBlueprintProductModel;
import com.ractoc.eve.crawler.model.YamlBlueprintSkillModel;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class BlueprintDeserializer extends StdDeserializer<YamlBlueprintModel> {

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
    public YamlBlueprintModel deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        try {
            JsonNode bpNode = jp.getCodec().readTree(jp);
            YamlBlueprintModel bp = new YamlBlueprintModel();
            bp.setId(getBlueprintId(bpNode));
            bp.setMaxProductionLimit(bpNode.get(MAX_PRODUCTION_LIMIT).intValue());
            deserializeActivities(bp, bpNode);
            return bp;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private int getBlueprintId(JsonNode bpNode) {
        return bpNode.get(BLUEPRINT_TYPE_ID).intValue();
    }

    private void deserializeActivities(YamlBlueprintModel bp, JsonNode bpNode) {
        JsonNode actNode = bpNode.get(ACTIVITIES);
        deserializeCopying(bp, actNode);
        deserializeInvention(bp, actNode);
        deserializeManufacturing(bp, actNode);
        deserializeResearchMaterialTime(bp, actNode);
        deserializeResearchTimeTime(bp, actNode);
    }

    private void deserializeCopying(YamlBlueprintModel bp, JsonNode actNode) {
        JsonNode copNode = actNode.get(COPYING);
        if (copNode != null) {
            bp.setCopyingTime(copNode.get(TIME).intValue());
        }
    }

    private void deserializeInvention(YamlBlueprintModel bp, JsonNode actNode) {
        JsonNode invNode = actNode.get(INVENTION);
        if (invNode != null) {
            bp.setInventionTime(invNode.get(TIME).intValue());
            deserializeMaterials(bp, invNode, bp.getInventionMaterials());
            deserializeProducts(bp, invNode, bp.getInventionProducts());
            deserializeSkills(bp, invNode, bp.getInventionSkills());
        }
    }

    private void deserializeManufacturing(YamlBlueprintModel bp, JsonNode actNode) {
        JsonNode manNode = actNode.get(MANUFACTURING);
        if (manNode != null) {
            bp.setManufacturingTime(manNode.get(TIME).intValue());
            deserializeMaterials(bp, manNode, bp.getManufacturingMaterials());
            deserializeProducts(bp, manNode, bp.getManufacturingProducts());
            deserializeSkills(bp, manNode, bp.getManufacturingSkills());
        }
    }

    private void deserializeResearchMaterialTime(YamlBlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, RESEARCH_MATERIAL);
        opt.ifPresent(node -> bp.setResearchMaterialTime(node.get(TIME).intValue()));
    }

    private void deserializeResearchTimeTime(YamlBlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, RESEARCH_TIME);
        opt.ifPresent(node -> bp.setResearchTimeTime(node.get(TIME).intValue()));
    }

    private Optional<JsonNode> getChildNode(JsonNode node, String child) {
        return Optional.ofNullable(node.get(child));
    }

    private void deserializeMaterials(YamlBlueprintModel bp, JsonNode node, Set<YamlBlueprintMaterialModel> mats) {
        JsonNode matNodes = node.get(MATERIALS);
        if (matNodes != null) {
            for (JsonNode matNode : matNodes) {
                YamlBlueprintMaterialModel.YamlBlueprintMaterialModelBuilder builder = YamlBlueprintMaterialModel.builder();
                builder.blueprintId(bp.getId());
                builder.typeId(matNode.get(TYPE_ID).intValue());
                builder.quantity(matNode.get(QUANTITY).intValue());
                YamlBlueprintMaterialModel bpMat = builder
                        .build();
                mats.add(bpMat);
            }
        }
    }

    private void deserializeProducts(YamlBlueprintModel bp, JsonNode node, Set<YamlBlueprintProductModel> prods) {
        JsonNode prodNodes = node.get(PRODUCTS);
        if (prodNodes != null) {
            for (JsonNode prodNode : prodNodes) {
                YamlBlueprintProductModel bpProd = YamlBlueprintProductModel.builder()
                        .blueprintId(bp.getId())
                        .typeId(prodNode.get(TYPE_ID).intValue())
                        .probability(prodNode.get(PROBABILITY) != null ? prodNode.get(PROBABILITY).floatValue() : 0.0f)
                        .quantity(prodNode.get(QUANTITY).intValue())
                        .build();
                prods.add(bpProd);
            }
        }
    }

    private void deserializeSkills(YamlBlueprintModel bp, JsonNode node, Set<YamlBlueprintSkillModel> skills) {
        JsonNode skillNodes = node.get(SKILLS);
        if (skillNodes != null) {
            for (JsonNode skillNode : skillNodes) {
                YamlBlueprintSkillModel bpSkill = YamlBlueprintSkillModel.builder()
                        .blueprintId(bp.getId())
                        .typeId(skillNode.get(TYPE_ID).intValue())
                        .level(skillNode.get(LEVEL).intValue())
                        .build();
                skills.add(bpSkill);
            }
        }
    }
}
