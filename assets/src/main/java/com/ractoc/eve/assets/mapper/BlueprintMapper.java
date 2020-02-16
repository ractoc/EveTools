package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.Blueprint;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.BlueprintImpl;
import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.jesi.model.GetCharactersCharacterIdBlueprints200Ok;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintMapper extends BaseMapper {
    BlueprintMapper INSTANCE = Mappers.getMapper(BlueprintMapper.class);

    BlueprintImpl modelToDb(BlueprintModel model);

    BlueprintModel dbToModel(Blueprint bp);

    @Mapping(source = "typeId", target = "id")
    @Mapping(source = "locationFlag.value", target = "locationFlag")
    BlueprintModel esiToModel(GetCharactersCharacterIdBlueprints200Ok esiModel);
}
