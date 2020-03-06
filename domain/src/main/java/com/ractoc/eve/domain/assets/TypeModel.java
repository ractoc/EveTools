package com.ractoc.eve.domain.assets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "Type Model", description = "Contains the Type model information")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeModel {
    private int id;
    private String name;
    private int groupId;
    private boolean published;
}
