package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.fleetmanager.RoleModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleModel dbToModel(Role role);
}
