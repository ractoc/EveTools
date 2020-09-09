package com.ractoc.eve.domain.fleetmanager;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "Simple Fleet Model", description = "Contains limited Fleet model information for use in listings")
public class SimpleFleetModel {
    private Integer id;
    private String name;
    private boolean restricted;
}
