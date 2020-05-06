package com.ractoc.eve.universe.handler;

import com.ractoc.eve.domain.universe.RegionModel;
import com.ractoc.eve.universe.mapper.RegionMapper;
import com.ractoc.eve.universe.service.RegionService;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class RegionHandler {

    private final RegionService regionService;
    private TransactionHandler transactionHandler;

    @Autowired
    public RegionHandler(TransactionHandler transactionHandler,
                         RegionService regionService) {
        this.regionService = regionService;
        this.transactionHandler = transactionHandler;
    }

    public List<RegionModel> getRegionList() {
        return regionService.getRegionList().map(RegionMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    public RegionModel getRegionById(Integer id) {
        return RegionMapper.INSTANCE.dbToModel(regionService.getRegionById(id));
    }

    public void saveRegion(List<RegionModel> regions) {
        transactionHandler.createAndAccept(tx -> {
            regionService.clearAllRegions();
            regions.stream()
                    .map(RegionMapper.INSTANCE::modelToDb)
                    .forEach(regionService::saveRegion);
            tx.commit();
        });
    }
}
