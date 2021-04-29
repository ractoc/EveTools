package com.ractoc.eve.crawler.mapper;

import com.ractoc.eve.domain.universe.RegionModel;
import com.ractoc.eve.jesi.model.GetUniverseRegionsRegionIdOk;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegionMapper {
    RegionMapper INSTANCE = Mappers.getMapper(RegionMapper.class);

    @Mapping(source = "regionId", target = "id", qualifiedByName = "id")
    RegionModel uApiToModel(GetUniverseRegionsRegionIdOk model);

    com.ractoc.eve.universe_client.model.RegionModel modelToAssetApi(RegionModel regionModel);
}
