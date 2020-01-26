package com.ractoc.eve.domain.assets;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@ApiModel(value = "Blueprint Material Model", description = "Contains the Blueprint Material model information")
public class BlueprintMaterialModel {
    @EqualsAndHashCode.Include
    private int blueprintId;
    @EqualsAndHashCode.Include
    private int typeId;
    private int quantity;

    private Double buyPrice;
    private Double sellPrice;

}
