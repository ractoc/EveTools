package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.Blueprint;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.BlueprintImpl;
import com.ractoc.eve.domain.assets.BlueprintModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintMapper {
    BlueprintMapper INSTANCE = Mappers.getMapper(BlueprintMapper.class);

    BlueprintImpl modelToDb(BlueprintModel model);

    BlueprintModel dbToModel(Blueprint bp);
}
