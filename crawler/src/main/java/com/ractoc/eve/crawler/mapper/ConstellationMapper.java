package com.ractoc.eve.crawler.mapper;

import com.ractoc.eve.domain.universe.ConstellationModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConstellationMapper {
    ConstellationMapper INSTANCE = Mappers.getMapper(ConstellationMapper.class);

    com.ractoc.eve.universe_client.model.ConstellationModel modelToApi(ConstellationModel model);
}
