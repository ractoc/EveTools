package com.ractoc.eve.domain.fleetmanager;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(value = "Registration Model", description = "Contains the Fleet Registration model information")
public class RegistrationModel {

    private Integer fleetId;
    private Integer characterId;
    private String name;

    private String fleetOwner;
    private String corporationName;
    private FleetModel fleet;

}
