package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.type.Type;
import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.ItemModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper extends BaseMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemModel dbToModel(Type type);
}
