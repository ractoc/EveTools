package com.ractoc.eve.character.mapper;

import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.jesi.model.GetCharactersCharacterIdBlueprints200Ok;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper
public interface BlueprintMapper {
    BlueprintMapper INSTANCE = Mappers.getMapper(BlueprintMapper.class);

    BlueprintModel esiToModel(GetCharactersCharacterIdBlueprints200Ok esiModel);

    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }
}
