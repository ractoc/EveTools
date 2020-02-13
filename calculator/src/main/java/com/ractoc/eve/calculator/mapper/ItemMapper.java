package com.ractoc.eve.calculator.mapper;


import com.ractoc.eve.assets_client.model.ItemModel;
import com.ractoc.eve.domain.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper extends BaseMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    com.ractoc.eve.domain.assets.ItemModel apiToModel(ItemModel esiModel);
}
