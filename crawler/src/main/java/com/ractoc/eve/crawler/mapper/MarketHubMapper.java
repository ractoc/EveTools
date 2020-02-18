package com.ractoc.eve.crawler.mapper;

import com.ractoc.eve.domain.universe.MarketHubModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MarketHubMapper {
    MarketHubMapper INSTANCE = Mappers.getMapper(MarketHubMapper.class);

    com.ractoc.eve.universe_client.model.MarketHubModel modelToAssetApi(MarketHubModel model);
}
