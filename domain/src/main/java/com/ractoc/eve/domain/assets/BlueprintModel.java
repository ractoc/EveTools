package com.ractoc.eve.domain.assets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ractoc.eve.domain.deserializers.BlueprintDeserializer;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "Blueprint Model", description = "Contains the Blueprint model information")
@JsonDeserialize(using = BlueprintDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlueprintModel {
    private int id;
    private String name;
    private ItemModel item;
    private Long locationId;
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

    private Set<BlueprintMaterialModel> inventionMaterials = new HashSet<>();
    private Set<BlueprintProductModel> inventionProducts = new HashSet<>();
    private Set<BlueprintSkillModel> inventionSkills = new HashSet<>();

    private Set<BlueprintMaterialModel> manufacturingMaterials = new HashSet<>();
    private Set<BlueprintProductModel> manufacturingProducts = new HashSet<>();
    private Set<BlueprintSkillModel> manufacturingSkills = new HashSet<>();
}