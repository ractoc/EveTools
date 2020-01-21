package com.ractoc.eve.assets.handler;

import com.ractoc.eve.assets.mapper.*;
import com.ractoc.eve.assets.service.BlueprintService;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class BlueprintHandler {

    private BlueprintService blueprintService;
    private TransactionHandler transactionHandler;

    @Autowired
    public BlueprintHandler(TransactionHandler transactionHandler,
                            BlueprintService blueprintService) {
        this.blueprintService = blueprintService;
        this.transactionHandler = transactionHandler;
    }

    public void saveBlueprints(List<? extends BlueprintModel> bps) {
        transactionHandler.createAndAccept(tx -> {
            bps.stream()
                    .map(BlueprintMapper.INSTANCE::modelToDb)
                    .forEach(blueprintService::saveBlueprint);

            bps.stream()
                    .map(BlueprintModel::getInventionMaterials)
                    .flatMap(Set::stream)
                    .map(BlueprintInventionMaterialMapper.INSTANCE::modelToDb)
                    .forEach(blueprintService::saveInventionMaterial);
            bps.stream()
                    .map(BlueprintModel::getInventionProducts)
                    .flatMap(Set::stream)
                    .map(BlueprintInventionProductMapper.INSTANCE::modelToDb)
                    .forEach(blueprintService::saveInventionProduct);
            bps.stream()
                    .map(BlueprintModel::getInventionSkills)
                    .flatMap(Set::stream)
                    .map(BlueprintInventionSkillMapper.INSTANCE::modelToDb)
                    .forEach(blueprintService::saveInventionSkill);

            bps.stream()
                    .map(BlueprintModel::getManufacturingMaterials)
                    .flatMap(Set::stream)
                    .map(BlueprintManufacturingMaterialMapper.INSTANCE::modelToDb)
                    .forEach(blueprintService::saveManufacturingMaterial);
            bps.stream()
                    .map(BlueprintModel::getManufacturingProducts)
                    .flatMap(Set::stream)
                    .map(BlueprintManufacturingProductMapper.INSTANCE::modelToDb)
                    .forEach(blueprintService::saveManufacturingProduct);
            bps.stream()
                    .map(BlueprintModel::getManufacturingSkills)
                    .flatMap(Set::stream)
                    .map(BlueprintManufacturingSkillMapper.INSTANCE::modelToDb)
                    .forEach(blueprintService::saveManufacturingSkill);

            tx.commit();
        });
    }

    public void clearAllBlueprints() {
        transactionHandler.createAndAccept(tx -> {
            blueprintService.clearAllBlueprints();
            tx.commit();
        });
    }
}
