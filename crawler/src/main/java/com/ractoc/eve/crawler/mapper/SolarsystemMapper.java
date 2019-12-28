package com.ractoc.eve.crawler.mapper;

import com.ractoc.eve.domain.universe.SolarsystemModel;
import com.ractoc.eve.jesi.model.GetUniverseSystemsSystemIdOk;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SolarsystemMapper {
    SolarsystemMapper INSTANCE = Mappers.getMapper(SolarsystemMapper.class);

    com.ractoc.eve.universe_client.model.SystemModel modelToApi(SolarsystemModel model);

    @Mapping(source = "systemId", target = "id", qualifiedByName = "id")
    @Mapping(source = "securityStatus", target = "securityRating", qualifiedByName = "securityRating")
    SolarsystemModel uApiToModel(GetUniverseSystemsSystemIdOk model);
}
