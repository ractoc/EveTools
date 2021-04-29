package com.ractoc.eve.domain.character;


import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "Blueprint Model", description = "Contains the Blueprint model information")
public class BlueprintListModel {
    private int id;
    private String name;
}
