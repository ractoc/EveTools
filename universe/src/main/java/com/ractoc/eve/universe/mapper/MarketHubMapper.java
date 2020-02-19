package com.ractoc.eve.universe.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.universe.MarketHubModel;
import com.ractoc.eve.universe.db.universe.eve_universe.market_hubs.MarketHubs;
import com.ractoc.eve.universe.db.universe.eve_universe.market_hubs.MarketHubsImpl;
import com.ractoc.eve.universe.db.universe.eve_universe.region.Region;
import com.ractoc.eve.universe.db.universe.eve_universe.solarsystem.Solarsystem;
import com.speedment.common.tuple.Tuple3;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MarketHubMapper extends BaseMapper {
    MarketHubMapper INSTANCE = Mappers.getMapper(MarketHubMapper.class);

    MarketHubsImpl modelToDb(MarketHubModel model);

    MarketHubModel dbToModel(MarketHubs marketHub);

    default MarketHubModel joinToModel(Tuple3<MarketHubs, Region, Solarsystem> join) {
        MarketHubModel model = dbToModel(join.get0());
        model.setRegionName(join.get1().getName());
        model.setSolarSystemName(join.get2().getName());
        return model;
    }
}
