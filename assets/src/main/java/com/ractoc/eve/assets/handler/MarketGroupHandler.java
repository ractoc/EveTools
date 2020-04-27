package com.ractoc.eve.assets.handler;

import com.ractoc.eve.assets.mapper.MarketGroupMapper;
import com.ractoc.eve.assets.service.BlueprintService;
import com.ractoc.eve.assets.service.MarketGroupService;
import com.ractoc.eve.domain.assets.MarketGroupModel;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MarketGroupHandler {

    private final BlueprintService blueprintService;
    private final MarketGroupService marketGroupService;
    private final TransactionHandler transactionHandler;

    @Autowired
    public MarketGroupHandler(TransactionHandler transactionHandler,
                              BlueprintService blueprintService,
                              MarketGroupService marketGroupService) {
        this.marketGroupService = marketGroupService;
        this.blueprintService = blueprintService;
        this.transactionHandler = transactionHandler;
    }

    public void saveMarketGroups(List<MarketGroupModel> marketGroups) {
        transactionHandler.createAndAccept(tx -> {
            marketGroupService.clearAllMarketGroups();
            marketGroups.stream()
                    .map(MarketGroupMapper.INSTANCE::modelToDb)
                    .forEach(marketGroupService::saveMarketGroup);
            tx.commit();
        });
    }

    public MarketGroupModel getMarketGroupForBlueprint(int blueprintId) {
        return null;
    }

    public List<MarketGroupModel> getMarketGroups(int parentGroupId) {
        return marketGroupService.getMarketGroups(parentGroupId)
                .map(MarketGroupMapper.INSTANCE::dbToModel)
                .map(mgm -> {
                    mgm.setChildren(getMarketGroups(mgm.getId()));
                    return mgm;
                })
                .collect(Collectors.toList());
    }
}
