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
@ApiModel(value = "Item Model", description = "Contains the Item model information")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemModel {
    private int id;
    private String name;
    private Double buyPrice;
    private Double sellPrice;
}
