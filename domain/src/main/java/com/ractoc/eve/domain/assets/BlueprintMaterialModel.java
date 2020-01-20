package com.ractoc.eve.domain.assets;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@ApiModel(value = "Blueprint Material Model", description = "Contains the Blueprint Material model information")
public class BlueprintMaterialModel {
    private int blueprintId;
    private int typeId;
    private int quantity;
}
