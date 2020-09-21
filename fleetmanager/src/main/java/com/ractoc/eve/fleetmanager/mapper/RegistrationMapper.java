package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.RegistrationModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.Registrations;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegistrationMapper extends BaseMapper {
    RegistrationMapper INSTANCE = Mappers.getMapper(RegistrationMapper.class);

    RegistrationModel dbToModel(Registrations registrations);
}
