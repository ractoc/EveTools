package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.FleetImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;

@Mapper
public interface FleetMapper extends BaseMapper {
    FleetMapper INSTANCE = Mappers.getMapper(FleetMapper.class);

    @Named("startToTimeStamp")
    static Timestamp startToTimeStamp(String dateTime) {
        return DateTimeUtil.convertDateTimeToTimeStamp(dateTime);
    }

    @Mapping(source = "start", target = "startDateTime", qualifiedByName = "startToTimeStamp")
    FleetImpl modelToDb(FleetModel model);

    FleetModel dbToModel(Fleet fleet);
}
