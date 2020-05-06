package com.ractoc.eve.universe.handler;

import com.ractoc.eve.domain.universe.MarketHubModel;
import com.ractoc.eve.universe.mapper.MarketHubMapper;
import com.ractoc.eve.universe.service.MarketHubService;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class MarketHubHandler {

    private final MarketHubService marketHubService;
    private TransactionHandler transactionHandler;

    @Autowired
    public MarketHubHandler(TransactionHandler transactionHandler,
                            MarketHubService marketHubService) {
        this.marketHubService = marketHubService;
        this.transactionHandler = transactionHandler;
    }

    public List<MarketHubModel> getMarketHubList() {
        return marketHubService.getMarketHubList().map(MarketHubMapper.INSTANCE::joinToModel).collect(Collectors.toList());
    }

    public void saveMarketHubs(List<MarketHubModel> marketHubs) {
        transactionHandler.createAndAccept(tx -> {
            marketHubService.clearAllMarketHubs();
            marketHubs.stream()
                    .map(MarketHubMapper.INSTANCE::modelToDb)
                    .forEach(marketHubService::saveMarketHub);
            tx.commit();
        });
    }
}
