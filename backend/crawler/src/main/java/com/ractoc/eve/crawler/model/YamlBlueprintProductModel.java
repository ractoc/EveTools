package com.ractoc.eve.crawler.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class YamlBlueprintProductModel {
    @EqualsAndHashCode.Include
    private int blueprintId;
    @EqualsAndHashCode.Include
    private int typeId;
    private float probability;
    private int quantity;
}
