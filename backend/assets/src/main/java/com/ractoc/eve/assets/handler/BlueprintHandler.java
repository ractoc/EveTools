package com.ractoc.eve.assets.handler;

import com.ractoc.eve.assets.mapper.*;
import com.ractoc.eve.assets.service.BlueprintService;
import com.ractoc.eve.assets.service.TypeService;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.user.filter.EveUserDetails;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BlueprintHandler {

    private BlueprintService blueprintService;
    private TypeService typeService;
    private TransactionHandler transactionHandler;

    @Autowired
    public BlueprintHandler(TransactionHandler transactionHandler,
                            BlueprintService blueprintService,
                            TypeService typeService) {
        this.blueprintService = blueprintService;
        this.transactionHandler = transactionHandler;
        this.typeService = typeService;
    }

    public void saveBlueprints(List<? extends BlueprintModel> bps) {
        transactionHandler.createAndAccept(tx -> {
            blueprintService.clearAllBlueprints();
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

    public BlueprintModel getBlueprint(Integer bpId) {
        BlueprintModel bp = BlueprintMapper.INSTANCE.dbToModel(blueprintService.getBlueprint(bpId));
        addNameToBlueprint(bp);

        bp.setInventionMaterials(blueprintService
                .getInventionMaterials(bpId)
                .stream()
                .map(BlueprintInventionMaterialMapper.INSTANCE::dbToModel)
                .collect(Collectors.toSet()));
        bp.setInventionProducts(blueprintService
                .getInventionProducts(bpId)
                .stream()
                .map(BlueprintInventionProductMapper.INSTANCE::dbToModel)
                .collect(Collectors.toSet()));
        bp.setInventionSkills(blueprintService
                .getInventionSkills(bpId)
                .stream()
                .map(BlueprintInventionSkillMapper.INSTANCE::dbToModel)
                .collect(Collectors.toSet()));

        bp.setManufacturingMaterials(blueprintService
                .getManufacturingMaterials(bpId)
                .stream()
                .map(BlueprintManufacturingMaterialMapper.INSTANCE::dbToModel)
                .collect(Collectors.toSet()));
        bp.setManufacturingProducts(blueprintService
                .getManufacturingProducts(bpId)
                .stream()
                .map(BlueprintManufacturingProductMapper.INSTANCE::dbToModel)
                .collect(Collectors.toSet()));
        bp.setManufacturingSkills(blueprintService
                .getManufacturingSkills(bpId)
                .stream()
                .map(BlueprintManufacturingSkillMapper.INSTANCE::dbToModel)
                .collect(Collectors.toSet()));

        return bp;
    }

    public List<BlueprintModel> getBlueprintsForCharacter(EveUserDetails eveUserDetails) {
        return blueprintService.getBlueprintsForCharacter(eveUserDetails.getCharId(), eveUserDetails.getAccessToken())
                .map(BlueprintMapper.INSTANCE::esiToModel)
                .map(this::addNameToBlueprint)
                .sorted((b1, b2) -> b1.getName().compareToIgnoreCase(b2.getName()))
                .collect(Collectors.toList());
    }

    public List<BlueprintModel> getBlueprintsForCorporation(EveUserDetails eveUserDetails) {
        return blueprintService.getBlueprintsForCorporation(eveUserDetails.getCharId(), eveUserDetails.getAccessToken())
                .map(BlueprintMapper.INSTANCE::esiToModel)
                .map(this::addNameToBlueprint)
                .sorted((b1, b2) -> b1.getName().compareToIgnoreCase(b2.getName()))
                .collect(Collectors.toList());
    }

    private BlueprintModel addNameToBlueprint(BlueprintModel blueprintModel) {
        blueprintModel.setName(typeService.getItemName(blueprintModel.getId()));
        return blueprintModel;
    }
}
