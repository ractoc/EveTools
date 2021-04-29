package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkillsImpl;
import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.BlueprintSkillModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintInventionSkillMapper extends BaseMapper {
    BlueprintInventionSkillMapper INSTANCE = Mappers.getMapper(BlueprintInventionSkillMapper.class);

    BlueprintInventionSkillsImpl modelToDb(BlueprintSkillModel model);

    BlueprintSkillModel dbToModel(BlueprintInventionSkills materials);
}
