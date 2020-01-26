package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.type.Type;
import com.ractoc.eve.assets.db.assets.eve_assets.type.TypeImpl;
import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.TypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TypeMapper extends BaseMapper {
    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    TypeImpl modelToDb(TypeModel model);

    TypeModel dbToModel(Type type);
}
