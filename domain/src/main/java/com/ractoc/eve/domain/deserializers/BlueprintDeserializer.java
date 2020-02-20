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
    public static final String TYPE_ID_V2 = "typeId";
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
        try {
            JsonNode bpNode = jp.getCodec().readTree(jp);
            BlueprintModel bp = new BlueprintModel();
            bp.setId(getBlueprintId(bpNode));
            if (bpNode.get("name") != null) {
                bp.setName(bpNode.get("name").asText());
            }
            if (bpNode.get("locationId") != null) {
                bp.setLocationId(bpNode.get("locationId").longValue());
            }
            bp.setMaxProductionLimit(bpNode.get(MAX_PRODUCTION_LIMIT).intValue());
            bp.setMaterialEfficiency(bpNode.get("materialEfficiency") != null ? bpNode.get("materialEfficiency").intValue() : 0);
            bp.setTimeEfficiency(bpNode.get("timeEfficiency") != null ? bpNode.get("timeEfficiency").intValue() : 0);
            deserializeActivities(bp, bpNode);
            return bp;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

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
        JsonNode actNode = bpNode;
        if (actOpt.isPresent()) {
            actNode = actOpt.get();
        }
        deserializeCopying(bp, actNode);
        deserializeInvention(bp, actNode);
        deserializeManufacturing(bp, actNode);
        deserializeResearchMaterialTime(bp, actNode);
        deserializeResearchTimeTime(bp, actNode);
    }

    private void deserializeCopying(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, COPYING);
        opt.ifPresent(node -> bp.setCopyingTime(node.get(TIME).intValue()));
    }

    private void deserializeInvention(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, INVENTION);
        JsonNode invNode = actNode;
        if (opt.isPresent()) {
            invNode = opt.get();
        }
        bp.setInventionTime(deserializeTime(invNode, INVENTION));
        deserializeMaterials(bp, invNode, bp.getInventionMaterials(), INVENTION);
        deserializeMaterials(bp, invNode, bp.getInventionMaterials(), INVENTION);
        deserializeProducts(bp, invNode, bp.getInventionProducts(), INVENTION);
        deserializeSkills(bp, invNode, bp.getInventionSkills(), INVENTION);
    }

    private void deserializeManufacturing(BlueprintModel bp, JsonNode actNode) {
        Optional<JsonNode> opt = getChildNode(actNode, MANUFACTURING);
        JsonNode manNode = actNode;
        if (opt.isPresent()) {
            manNode = opt.get();
        }
        bp.setManufacturingTime(deserializeTime(manNode, MANUFACTURING));
        deserializeMaterials(bp, manNode, bp.getManufacturingMaterials(), MANUFACTURING);
        deserializeProducts(bp, manNode, bp.getManufacturingProducts(), MANUFACTURING);
        deserializeSkills(bp, manNode, bp.getManufacturingSkills(), MANUFACTURING);
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

    private int deserializeTime(JsonNode actNode, String context) {
        int time = 0;
        if (actNode.get(TIME) != null) {
            time = actNode.get(TIME).intValue();
        } else if (actNode.get(context + TIME.replaceFirst("t", "T")) != null) {
            time = actNode.get(context + TIME.replaceFirst("t", "T")).intValue();
        }
        return time;
    }

    private void deserializeMaterials(BlueprintModel bp, JsonNode node, Set<BlueprintMaterialModel> mats, String context) {
        Optional<JsonNode> matOpt = getChildNode(node, MATERIALS);
        JsonNode matNodes = null;
        if (matOpt.isPresent()) {
            matNodes = matOpt.get();
        } else {
            matNodes = node.get(context + MATERIALS.replaceFirst("m", "M"));
        }
        if (matNodes != null) {
            for (JsonNode matNode : matNodes) {
                BlueprintMaterialModel.BlueprintMaterialModelBuilder builder = BlueprintMaterialModel.builder();
                builder.blueprintId(bp.getId());
                builder.typeId(getTypeId(matNode));
                builder.quantity(matNode.get(QUANTITY).intValue());
                BlueprintMaterialModel bpMat = builder
                        .build();
                mats.add(bpMat);
            }
        }
    }

    private void deserializeProducts(BlueprintModel bp, JsonNode node, Set<BlueprintProductModel> prods, String context) {
        Optional<JsonNode> prodOpt = getChildNode(node, PRODUCTS);
        JsonNode prodNodes = null;
        if (prodOpt.isPresent()) {
            prodNodes = prodOpt.get();
        } else {
            prodNodes = node.get(context + MATERIALS.replaceFirst("p", "P"));
        }
        if (prodNodes != null) {
            for (JsonNode prodNode : prodNodes) {
                BlueprintProductModel bpProd = BlueprintProductModel.builder()
                        .blueprintId(bp.getId())
                        .typeId(getTypeId(prodNode))
                        .probability(prodNode.get(PROBABILITY) != null ? prodNode.get(PROBABILITY).floatValue() : 0.0f)
                        .quantity(prodNode.get(QUANTITY).intValue())
                        .build();
                prods.add(bpProd);
            }
        }
    }

    private void deserializeSkills(BlueprintModel bp, JsonNode node, Set<BlueprintSkillModel> skills, String context) {
        Optional<JsonNode> skillOpt = getChildNode(node, SKILLS);
        JsonNode skillNodes = null;
        if (skillOpt.isPresent()) {
            skillNodes = skillOpt.get();
        } else {
            skillNodes = node.get(context + MATERIALS.replaceFirst("p", "P"));
        }
        if (skillNodes != null) {
            for (JsonNode skillNode : skillNodes) {
                BlueprintSkillModel bpSkill = BlueprintSkillModel.builder()
                        .blueprintId(bp.getId())
                        .typeId(getTypeId(skillNode))
                        .level(skillNode.get(LEVEL).intValue())
                        .build();
                skills.add(bpSkill);
            }
        }
    }

    private int getTypeId(JsonNode typeNode) {
        if (typeNode.get(TYPE_ID) != null) {
            return typeNode.get(TYPE_ID).intValue();
        }
        return typeNode.get(TYPE_ID_V2).intValue();
    }
}
