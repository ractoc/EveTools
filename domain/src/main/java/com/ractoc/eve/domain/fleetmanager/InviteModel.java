package com.ractoc.eve.domain.fleetmanager;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(value = "Invite Model", description = "Contains the Fleet Invitation model information")
public class InviteModel {

    private Integer fleetId;
    private String key;
    private String name;
    private Integer charId;
    private Integer corpId;
    private String additionalInfo;

    private FleetModel fleet;

}
