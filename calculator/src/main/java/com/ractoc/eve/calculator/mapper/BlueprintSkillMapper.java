package com.ractoc.eve.calculator.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.BlueprintSkillModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintSkillMapper extends BaseMapper {
    BlueprintSkillMapper INSTANCE = Mappers.getMapper(BlueprintSkillMapper.class);

    BlueprintSkillModel dbToModel(com.ractoc.eve.assets_client.model.BlueprintSkillModel materials);
}
