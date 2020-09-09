package com.ractoc.eve.domain.fleetmanager;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "Fleet Model", description = "Contains the Fleet model information")
public class FleetModel {
    private Integer id;
    private String name;
    private Integer owner;
    private String start;
    private Integer duration;
    private boolean corporationRestricted;
}
