package com.ractoc.eve.crawler.mapper;

import com.ractoc.eve.domain.assets.BlueprintModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintMapper {
    BlueprintMapper INSTANCE = Mappers.getMapper(BlueprintMapper.class);

    com.ractoc.eve.assets_client.model.BlueprintModel modelToAssetApi(BlueprintModel model);
}
