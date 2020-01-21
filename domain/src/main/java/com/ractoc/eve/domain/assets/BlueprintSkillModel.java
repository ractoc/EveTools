package com.ractoc.eve.domain.assets;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@ApiModel(value = "Blueprint Skill Model", description = "Contains the Blueprint Skill model information")
public class BlueprintSkillModel {
    @EqualsAndHashCode.Include
    private int blueprintId;
    @EqualsAndHashCode.Include
    private int typeId;
    private int level;
}
