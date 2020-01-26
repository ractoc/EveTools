package com.ractoc.eve.calculator.handler;

import com.ractoc.eve.calculator.mapper.BlueprintMapper;
import com.ractoc.eve.calculator.service.BlueprintService;
import com.ractoc.eve.calculator.service.CalculatorService;
import com.ractoc.eve.domain.assets.BlueprintModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculatorHandler {

    @Autowired
    private BlueprintService blueprintService;
    @Autowired
    private CalculatorService calculatorService;

    public BlueprintModel calculateBlueprintPrices(Integer bpId, Integer regionId, Long locationId) {
        BlueprintModel bp = BlueprintMapper.INSTANCE.apiToModel(blueprintService.getBlueprint(bpId));
        calculatorService.calculateMaterialCost(bp, regionId, locationId);
        return bp;
    }

}
