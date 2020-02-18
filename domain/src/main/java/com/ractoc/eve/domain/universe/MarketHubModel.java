package com.ractoc.eve.domain.universe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "MarketHub Model", description = "Contains the MarketHub model information")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketHubModel {
    private Integer id;
    private Integer regionId;
    private String regionName;
    private Integer solarSystemId;
    private Integer solarSystemName;
}
