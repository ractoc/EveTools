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
@ApiModel(value = "Region Model", description = "Contains the region model information")
public class RegionModel {
    @ApiModelProperty(value = "The id of the region.")
    @NotNull
    private Integer id;
    @ApiModelProperty(value = "The name of the region.")
    @NotBlank
    private String name;
}
