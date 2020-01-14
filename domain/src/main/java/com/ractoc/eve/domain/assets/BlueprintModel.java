package com.ractoc.eve.domain.assets;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@ApiModel(value = "Blueprint Model", description = "Contains the Blueprint model information")
public class BlueprintModel {

    private Long itemId;
    private Integer materialEfficiency;
    private Integer quantity;
    private Integer runs;
    private Integer timeEfficiency;

    /**
     * References a solar system, station or item_id if this blueprint is located within a container. If the return value is an item_id, then the Character AssetList API must be queried to find the container using the given item_id to determine the correct location of the Blueprint.
     **/
    private Long locationId;
}
