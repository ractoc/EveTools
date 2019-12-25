package com.ractoc.eve.crawler.mapper;

import com.ractoc.eve.domain.universe.RegionModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegionMapper {
    RegionMapper INSTANCE = Mappers.getMapper(RegionMapper.class);

    com.ractoc.eve.universe_client.model.RegionModel modelToApi(RegionModel model);
}
