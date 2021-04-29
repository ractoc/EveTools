package com.ractoc.eve.crawler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ractoc.eve.crawler.model.deserializers.BlueprintDeserializer;
import com.ractoc.eve.domain.assets.ItemModel;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonDeserialize(using = BlueprintDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class YamlBlueprintModel {
    private int id;
    private String name;
    private ItemModel item;
    private Long locationId;
    private String locationFlag;
    private int maxProductionLimit;
    private int copyingTime;
    private int inventionTime;
    private int manufacturingTime;
    private int researchMaterialTime;
    private int researchTimeTime;
    private Integer materialEfficiency;
    private Integer timeEfficiency;
    private Integer runs;
    private Integer quantity;
    private double mineralBuyPrice;
    private double mineralSellPrice;
    private double jobInstallationCosts;

    private Set<YamlBlueprintMaterialModel> inventionMaterials = new HashSet<>();
    private Set<YamlBlueprintProductModel> inventionProducts = new HashSet<>();
    private Set<YamlBlueprintSkillModel> inventionSkills = new HashSet<>();

    private Set<YamlBlueprintMaterialModel> manufacturingMaterials = new HashSet<>();
    private Set<YamlBlueprintProductModel> manufacturingProducts = new HashSet<>();
    private Set<YamlBlueprintSkillModel> manufacturingSkills = new HashSet<>();
}
