package com.ractoc.eve.domain.fleetmanager;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "Type Model", description = "Contains the Type model information")
public class TypeModel {
    private Integer id;
    private String name;
    private String description;

    private List<RoleModel> roles;
}
