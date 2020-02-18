package com.ractoc.eve.crawler.mapper;

import com.ractoc.eve.domain.assets.TypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TypeMapper {
    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    com.ractoc.eve.assets_client.model.TypeModel modelToAssetApi(TypeModel model);
}
