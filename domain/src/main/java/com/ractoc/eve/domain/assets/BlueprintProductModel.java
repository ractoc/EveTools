package com.ractoc.eve.domain.assets;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@ApiModel(value = "Blueprint Product Model", description = "Contains the Blueprint Product model information")
public class BlueprintProductModel {
    @EqualsAndHashCode.Include
    private int blueprintId;
    @EqualsAndHashCode.Include
    private int typeId;
    private float probability;
    private int quantity;
}
