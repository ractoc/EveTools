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
@ApiModel(value = "Constellation Model", description = "Contains the constellation model information")
public class ConstellationModel {
    @ApiModelProperty(value = "The id of the constellation.")
    @NotNull
    private Integer id;
    @ApiModelProperty(value = "The name of the region.")
    @NotBlank
    private String name;
    @ApiModelProperty(value = "The id of the region this constellation is part of.")
    @NotNull
    private Integer regionId;
}
