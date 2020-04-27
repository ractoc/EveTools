package com.ractoc.eve.assets.service;

import com.ractoc.eve.assets.db.assets.eve_assets.market_group.MarketGroup;
import com.ractoc.eve.assets.db.assets.eve_assets.market_group.MarketGroupManager;
import com.ractoc.eve.assets.db.assets.eve_assets.market_group.generated.GeneratedMarketGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MarketGroupService {

    private MarketGroupManager marketGroupManager;

    @Autowired
    public MarketGroupService(MarketGroupManager marketGroupManager) {
        this.marketGroupManager = marketGroupManager;
    }

    public void saveMarketGroup(MarketGroup marketGroup) {
        marketGroupManager.persist(marketGroup);
    }

    public void clearAllMarketGroups() {
        List<MarketGroup> marketGroups = marketGroupManager.stream().collect(Collectors.toList());
        marketGroups.forEach(marketGroupManager.remover());
    }

    public Stream<MarketGroup> getMarketGroups(int parentGroupId) {
        return marketGroupManager.stream().filter(GeneratedMarketGroup.PARENT_GROUP_ID.equal(parentGroupId));
    }
}
