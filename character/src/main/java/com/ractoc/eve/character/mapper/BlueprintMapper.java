package com.ractoc.eve.character.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.character.BlueprintListModel;
import com.ractoc.eve.jesi.model.GetCharactersCharacterIdBlueprints200Ok;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintMapper extends BaseMapper {
    BlueprintMapper INSTANCE = Mappers.getMapper(BlueprintMapper.class);

    @Mapping(source = "typeId", target = "id")
    BlueprintListModel esiToModel(GetCharactersCharacterIdBlueprints200Ok esiModel);
}
