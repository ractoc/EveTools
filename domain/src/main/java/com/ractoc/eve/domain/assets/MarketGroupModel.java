package com.ractoc.eve.domain.assets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "Market Group Model", description = "Contains the Market Group model information")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketGroupModel {
    private int id;
    private String name;
    private int parentGroupId;
    private List<MarketGroupModel> children;
}
