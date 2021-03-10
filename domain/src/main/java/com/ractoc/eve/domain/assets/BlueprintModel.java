package com.ractoc.eve.domain.assets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlueprintModel {
    private int id;
    private String name;
    private double buyPrice;
    private ItemModel item;
    private Long locationId;
    private String locationFlag;
    private int maxProductionLimit;
    private int copyingTime;
    private int inventionTime;
    private int manufacturingTime;
    private int researchMaterialTime;
    private int researchTimeTime;
    private Integer materialEfficiency = 0;
    private Integer timeEfficiency = 0;
    private Integer runs = 1;
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

    public Integer getMaterialEfficiency() {
        if (materialEfficiency == null) {
            return 0;
        } else {
            return materialEfficiency;
        }
    }
}
