package com.ractoc.eve.universe.mapper;

import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.universe.SolarsystemModel;
import com.ractoc.eve.universe.db.universe.eve_universe.solarsystem.Solarsystem;
import com.ractoc.eve.universe.db.universe.eve_universe.solarsystem.SolarsystemImpl;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SolarsystemMapper extends BaseMapper {
    SolarsystemMapper INSTANCE = Mappers.getMapper(SolarsystemMapper.class);

    SolarsystemImpl modelToDb(SolarsystemModel model);

    SolarsystemModel dbToModel(Solarsystem constellation);
}
