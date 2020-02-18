package com.ractoc.eve.universe.service;

import com.ractoc.eve.universe.db.universe.eve_universe.market_hubs.MarketHubs;
import com.ractoc.eve.universe.db.universe.eve_universe.market_hubs.MarketHubsManager;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class MarketHubService {

    private final MarketHubsManager marketHubManager;

    @Autowired
    public MarketHubService(MarketHubsManager marketHubManager) {
        this.marketHubManager = marketHubManager;
    }

    public Stream<MarketHubs> getMarketHubList() {
        return marketHubManager.stream();
    }

    public void saveMarketHub(MarketHubs marketHub) {
        try {
            marketHubManager.persist(marketHub);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to save marketHub " + marketHub.getId(), e);
        }
    }

    public void clearAllMarketHubs() {
        List<MarketHubs> marketHubs = marketHubManager.stream().collect(Collectors.toList());
        marketHubs.forEach(marketHubManager.remover());
    }
}
