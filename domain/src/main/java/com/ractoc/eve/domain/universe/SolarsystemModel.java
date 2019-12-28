package com.ractoc.eve.domain.universe;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(value = "System Model", description = "Contains the system model information")
public class SolarsystemModel {
    @ApiModelProperty(value = "The id of the system.")
    @NotNull
    private Integer id;
    @ApiModelProperty(value = "The name of the system.")
    @NotBlank
    private String name;
    @ApiModelProperty(value = "The security class of the system.")
    private String securityClass;
    @ApiModelProperty(value = "The security rating of the system.")
    @NotNull
    private Float securityRating;
    @ApiModelProperty(value = "The id of the constellation this system is part of.")
    @NotNull
    private Integer constellationId;

}
