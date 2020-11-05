package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.RoleModel;
import com.ractoc.eve.domain.fleetmanager.TypeModel;
import com.ractoc.eve.fleetmanager.config.SpringContext;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.types.Types;
import com.ractoc.eve.fleetmanager.service.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface TypeMapper extends BaseMapper {
    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    private static RoleService getRoleService() {
        return SpringContext.getBean(RoleService.class);
    }

    @Named("idToRoles")
    static List<RoleModel> idToRoles(int typeId) {
        return getRoleService().getRolesForTypesId(typeId).map(RoleMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    @Mapping(source = "id", target = "roles", qualifiedByName = "idToRoles")
    TypeModel dbToModel(Types types);
}
