package com.ractoc.eve.calculator.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.BlueprintMaterialModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintMaterialMapper extends BaseMapper {
    BlueprintMaterialMapper INSTANCE = Mappers.getMapper(BlueprintMaterialMapper.class);

    BlueprintMaterialModel apiToModel(com.ractoc.eve.assets_client.model.BlueprintMaterialModel materials);
}
