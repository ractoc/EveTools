package com.ractoc.eve.crawler.mapper;

import com.ractoc.eve.domain.universe.ConstellationModel;
import com.ractoc.eve.jesi.model.GetUniverseConstellationsConstellationIdOk;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConstellationMapper {
    ConstellationMapper INSTANCE = Mappers.getMapper(ConstellationMapper.class);

    @Mapping(source = "constellationId", target = "id", qualifiedByName = "id")
    ConstellationModel uApiToModel(GetUniverseConstellationsConstellationIdOk model);

    com.ractoc.eve.universe_client.model.ConstellationModel modelToAssetApi(ConstellationModel constellationModel);
}
