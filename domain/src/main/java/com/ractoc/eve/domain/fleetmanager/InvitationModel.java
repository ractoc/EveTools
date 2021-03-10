package com.ractoc.eve.domain.fleetmanager;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(value = "Invitation Model", description = "Contains the information for creating an invitation")
public class InvitationModel {

    private Integer id;
    private String type;
    private Integer fleetId;
    private Integer inviteeId;

}
