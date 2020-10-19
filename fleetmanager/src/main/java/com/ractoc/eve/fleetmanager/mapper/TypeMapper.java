package com.ractoc.eve.fleetmanager.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.TypeModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.types.Types;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TypeMapper extends BaseMapper {
    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    TypeModel dbToModel(Types types);
}
