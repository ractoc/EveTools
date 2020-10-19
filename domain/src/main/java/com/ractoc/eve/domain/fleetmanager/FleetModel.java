package com.ractoc.eve.domain.fleetmanager;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
@ApiModel(value = "Fleet Model", description = "Contains the Fleet model information")
public class FleetModel {
    private Integer id;
    private String name;
    private Integer owner;
    private String start;
    private Integer duration;
    private boolean corporationRestricted;

    private Integer typeId;
    private TypeModel type;

    @Length(max = 1000, message = "invite text can not exceed a 1000 characters")
    private String inviteText;
}
