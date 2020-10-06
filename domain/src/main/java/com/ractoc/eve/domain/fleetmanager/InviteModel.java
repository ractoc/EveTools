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
public class InviteModel {

    private Integer fleetId;
    private String key;
    private Integer characterId;
    private Integer corporationId;
    private String name;
    private String additionalInfo;

    private String characterName;
    private String corporationName;
    private FleetModel fleet;

}
