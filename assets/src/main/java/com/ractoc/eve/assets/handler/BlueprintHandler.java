package com.ractoc.eve.assets.handler;

import com.ractoc.eve.assets.mapper.BlueprintInventionMaterialMapper;
import com.ractoc.eve.assets.mapper.BlueprintMapper;
import com.ractoc.eve.assets.service.BlueprintInventionMaterialService;
import com.ractoc.eve.assets.service.BlueprintService;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BlueprintHandler {

    private BlueprintService blueprintService;
    private BlueprintInventionMaterialService bimService;
    private TransactionHandler transactionHandler;

    @Autowired
    public BlueprintHandler(TransactionHandler transactionHandler,
                            BlueprintService blueprintService,
                            BlueprintInventionMaterialService bimService) {
        this.blueprintService = blueprintService;
        this.bimService = bimService;
        this.transactionHandler = transactionHandler;
    }

    public void saveBlueprints(List<? extends BlueprintModel> bps) {
        transactionHandler.createAndAccept(tx -> {
            bps.stream()
                    .map(BlueprintMapper.INSTANCE::modelToDb)
                    .forEach(blueprintService::saveBlueprint);
            bps.stream()
                    .map(BlueprintModel::getInventionMaterials)
                    .flatMap(List::stream)
                    .map(BlueprintInventionMaterialMapper.INSTANCE::modelToDb)
                    .forEach(bimService::saveMaterial);
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
