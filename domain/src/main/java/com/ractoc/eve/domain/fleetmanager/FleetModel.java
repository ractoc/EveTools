package com.ractoc.eve.domain.fleetmanager;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
@ApiModel(value = "Fleet Model", description = "Contains the Fleet model information")
public class FleetModel {
    private Integer id;
    @NonNull
    private String name;
    @Length(max = 1000, message = "Description can not exceed a 1000 characters")
    private String description;

    private Integer owner;
    @NonNull
    private String start;
    private Integer duration;

    private boolean restricted = false;

    @NonNull
    private TypeModel type;

}
