package com.ractoc.eve.crawler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ractoc.eve.crawler.model.deserializers.MarketGroupDeserializer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonDeserialize(using = MarketGroupDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class YamlMarketGroupModel {
    private int id;
    private String name;
    private int parentGroupId;
}
