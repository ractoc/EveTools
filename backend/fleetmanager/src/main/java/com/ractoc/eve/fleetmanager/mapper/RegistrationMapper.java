package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.RegistrationModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.Registrations;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.RegistrationsImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;

@Mapper
public interface RegistrationMapper extends BaseMapper {
    RegistrationMapper INSTANCE = Mappers.getMapper(RegistrationMapper.class);

    @Named("startEndToTimeStamp")
    static Timestamp startEndToTimeStamp(String dateTime) {
        return DateTimeUtil.convertDateTimeToTimeStamp(dateTime);
    }

    @Mapping(source = "start", target = "startDateTime", qualifiedByName = "startEndToTimeStamp")
    @Mapping(source = "end", target = "endDateTime", qualifiedByName = "startEndToTimeStamp")
    RegistrationsImpl modelToDB(RegistrationModel model);

    RegistrationModel dbToModel(Registrations registrations);
}
