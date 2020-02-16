package com.ractoc.eve.calculator.handler;

import com.ractoc.eve.calculator.mapper.BlueprintMapper;
import com.ractoc.eve.calculator.mapper.ItemMapper;
import com.ractoc.eve.calculator.service.BlueprintService;
import com.ractoc.eve.calculator.service.CalculatorService;
import com.ractoc.eve.calculator.service.ItemService;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.domain.assets.ItemModel;
import com.ractoc.eve.user.filter.EveUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculatorHandler {

    @Autowired
    private BlueprintService blueprintService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CalculatorService calculatorService;

    public BlueprintModel calculateBlueprintPrices(Integer regionId, Long locationId, BlueprintModel blueprint, Integer runs, EveUserDetails userDetails) {
        BlueprintModel bp = BlueprintMapper.INSTANCE.apiToModel(blueprintService.getBlueprint(blueprint.getId()));
        blueprint.setManufacturingMaterials(bp.getManufacturingMaterials());
        blueprint.setManufacturingProducts(bp.getManufacturingProducts());
        calculatorService.calculateMaterialPrices(blueprint, regionId, locationId, runs);
        ItemModel item = ItemMapper.INSTANCE.apiToModel(itemService.getItemForBlueprint(blueprint.getId()));
        calculatorService.calculateItemPrices(item, regionId, locationId, runs);
        calculatorService.calculateSalesTax(item, userDetails.getCharId(), userDetails.getAccessToken());
        calculatorService.calculateBrokerFee(item, userDetails.getCharId(), userDetails.getAccessToken());
        blueprint.setItem(item);
        calculatorService.calculateJobInstallationCosts(blueprint, userDetails.getCharId(), userDetails.getAccessToken());
        // profitability:
        // totalMineralSellPrice > totalMineralBuyPrice = just sell the minerals
        // itemBuyPrice < itemSellPrice = just buy and sell the item
        // totalMineralBuyCost < itemBuyPrice = build and sell the item
        return blueprint;
    }

}
