package com.ractoc.eve.universe.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.universe.RegionModel;
import com.ractoc.eve.universe.db.universe.eve_universe.region.Region;
import com.ractoc.eve.universe.db.universe.eve_universe.region.RegionImpl;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegionMapper extends BaseMapper {
    RegionMapper INSTANCE = Mappers.getMapper(RegionMapper.class);

    RegionImpl modelToDb(RegionModel model);

    RegionModel dbToModel(Region region);
}
