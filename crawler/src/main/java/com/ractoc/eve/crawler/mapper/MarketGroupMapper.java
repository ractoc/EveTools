package com.ractoc.eve.crawler.mapper;

import com.ractoc.eve.crawler.model.YamlMarketGroupModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MarketGroupMapper {
    MarketGroupMapper INSTANCE = Mappers.getMapper(MarketGroupMapper.class);

    com.ractoc.eve.assets_client.model.MarketGroupModel modelToAssetApi(YamlMarketGroupModel model);
}
