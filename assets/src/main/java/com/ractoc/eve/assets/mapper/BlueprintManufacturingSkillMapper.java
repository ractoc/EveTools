package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkillsImpl;
import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.BlueprintSkillModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintManufacturingSkillMapper extends BaseMapper {
    BlueprintManufacturingSkillMapper INSTANCE = Mappers.getMapper(BlueprintManufacturingSkillMapper.class);

    BlueprintManufacturingSkillsImpl modelToDb(BlueprintSkillModel model);

    BlueprintSkillModel dbToModel(BlueprintManufacturingSkills materials);
}
