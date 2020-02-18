package com.ractoc.eve.universe.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.universe.MarketHubModel;
import com.ractoc.eve.universe.db.universe.eve_universe.market_hubs.MarketHubs;
import com.ractoc.eve.universe.db.universe.eve_universe.market_hubs.MarketHubsImpl;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MarketHubMapper extends BaseMapper {
    MarketHubMapper INSTANCE = Mappers.getMapper(MarketHubMapper.class);

    MarketHubsImpl modelToDb(MarketHubModel model);

    MarketHubModel dbToModel(MarketHubs marketHub);
}
