package com.ractoc.eve.universe.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.universe.ConstellationModel;
import com.ractoc.eve.universe.db.universe.eve_universe.constellation.Constellation;
import com.ractoc.eve.universe.db.universe.eve_universe.constellation.ConstellationImpl;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConstellationMapper extends BaseMapper {
    ConstellationMapper INSTANCE = Mappers.getMapper(ConstellationMapper.class);

    ConstellationImpl modelToDb(ConstellationModel model);

    ConstellationModel dbToModel(Constellation constellation);
}
