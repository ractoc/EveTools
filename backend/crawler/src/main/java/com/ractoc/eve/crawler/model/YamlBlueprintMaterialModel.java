package com.ractoc.eve.crawler.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class YamlBlueprintMaterialModel {
    @EqualsAndHashCode.Include
    private int blueprintId;
    @EqualsAndHashCode.Include
    private int typeId;
    private int quantity;
    private int calculatedTotalQuantity;

    private Double buyPrice;
    private Double sellPrice;

}
