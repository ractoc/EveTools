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

    public static final String TYPE_CHARACTER = "character";
    public static final String TYPE_CORPORATION = "corporation";

    private Integer fleetId;
    private Integer id;
    private String key;
    private String type;
    private String name;
    private String additionalInfo;

    private FleetModel fleet;

}
