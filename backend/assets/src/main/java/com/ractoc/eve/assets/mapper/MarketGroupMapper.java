package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.market_group.MarketGroup;
import com.ractoc.eve.assets.db.assets.eve_assets.market_group.MarketGroupImpl;
import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.MarketGroupModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MarketGroupMapper extends BaseMapper {
    MarketGroupMapper INSTANCE = Mappers.getMapper(MarketGroupMapper.class);

    MarketGroupImpl modelToDb(MarketGroupModel model);

    MarketGroupModel dbToModel(MarketGroup marketGroup);
}
