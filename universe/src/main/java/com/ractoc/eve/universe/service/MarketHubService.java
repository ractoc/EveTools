package com.ractoc.eve.universe.service;

import com.ractoc.eve.universe.db.UniverseApplication;
import com.ractoc.eve.universe.db.universe.eve_universe.market_hubs.MarketHubs;
import com.ractoc.eve.universe.db.universe.eve_universe.market_hubs.MarketHubsManager;
import com.ractoc.eve.universe.db.universe.eve_universe.region.Region;
import com.ractoc.eve.universe.db.universe.eve_universe.solarsystem.Solarsystem;
import com.speedment.common.tuple.Tuple3;
import com.speedment.common.tuple.Tuples;
import com.speedment.runtime.core.exception.SpeedmentException;
import com.speedment.runtime.join.Join;
import com.speedment.runtime.join.JoinComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class MarketHubService {

    private final MarketHubsManager marketHubManager;
    private final UniverseApplication application;

    @Autowired
    public MarketHubService(UniverseApplication application, MarketHubsManager marketHubManager) {
        this.application = application;
        this.marketHubManager = marketHubManager;
    }

    public Stream<Tuple3<MarketHubs, Region, Solarsystem>> getMarketHubList() {
        JoinComponent joinComponent = application.getOrThrow(JoinComponent.class);
        Join<Tuple3<MarketHubs, Region, Solarsystem>> join = joinComponent.from(marketHubManager.IDENTIFIER)
                .innerJoinOn(Region.ID).equal(MarketHubs.REGION_ID)
                .innerJoinOn(Solarsystem.ID).equal(MarketHubs.SOLAR_SYSTEM_ID)
                .build(Tuples::of);
        System.out.println(join);
        return join.stream();
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
