package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.SimpleFleetModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SimpleFleetMapper extends BaseMapper {
    SimpleFleetMapper INSTANCE = Mappers.getMapper(SimpleFleetMapper.class);

    SimpleFleetModel dbToModel(Fleet fleet);
}
