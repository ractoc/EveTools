package com.ractoc.eve.calculator.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.BlueprintProductModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintProductMapper extends BaseMapper {
    BlueprintProductMapper INSTANCE = Mappers.getMapper(BlueprintProductMapper.class);

    BlueprintProductModel apiToModel(com.ractoc.eve.assets_client.model.BlueprintProductModel materials);
}
