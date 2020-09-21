package com.ractoc.eve.domain.fleetmanager;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "Invite Model", description = "Contains the Fleet Invitation model information")
public class RegistrationModel {

    private Integer fleetId;
    private Integer characterId;
    private String name;

    private String fleetOwner;
    private String corporationName;
    private FleetModel fleet;

}
