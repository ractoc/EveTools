package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.FleetImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.OptionalInt;

@Mapper
public interface FleetMapper extends BaseMapper {
    FleetMapper INSTANCE = Mappers.getMapper(FleetMapper.class);

    @Named("corporationIdToCorporationRestricted")
    public static boolean corporationIdToCorporationRestricted(OptionalInt corporationId) {
        return corporationId.isPresent() && corporationId.getAsInt() > 0;
    }

    FleetImpl modelToDb(FleetModel model);

    @Mapping(source = "corporationId", target = "corporationRestricted", qualifiedByName = "corporationIdToCorporationRestricted")
    FleetModel dbToModel(Fleet fleet);
}
