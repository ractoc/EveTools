package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.TypeModel;
import com.ractoc.eve.domain.universe.SolarsystemModel;
import com.ractoc.eve.fleetmanager.config.SpringContext;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.FleetImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.type.Type;
import com.ractoc.eve.fleetmanager.service.TypeService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.util.Optional;

@Mapper
public interface FleetMapper extends BaseMapper {
    FleetMapper INSTANCE = Mappers.getMapper(FleetMapper.class);

    @Named("startToTimeStamp")
    static Timestamp startToTimeStamp(String dateTime) {
        return DateTimeUtil.convertDateTimeToTimeStamp(dateTime);
    }

    @Named("typeIdToType")
    static TypeModel typeIdToType(int typeId) {
        return getTypeService().getTypesById(typeId).map(TypeMapper.INSTANCE::dbToModel).orElse(null);
    }

    @Named("typeToTypeId")
    static int typeToTypeId(TypeModel type) {
        return type.getId();
    }

    private static TypeService getTypeService() {
        return SpringContext.getBean(TypeService.class);
    }

    @Mapping(source = "start", target = "startDateTime", qualifiedByName = "startToTimeStamp")
    @Mapping(source = "type", target = "typeId", qualifiedByName = "typeToTypeId")
    FleetImpl modelToDb(FleetModel model);

    @Mapping(source = "typeId", target = "type", qualifiedByName = "typeIdToType")
    FleetModel dbToModel(Fleet fleet);
}
