package com.ractoc.eve.domain.fleetmanager;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@ApiModel(value = "Role Model", description = "Contains the Fleet Role model information")
public class RoleModel {

    private Integer id;
    private String name;
    private String description;
    private Integer amount;
}
