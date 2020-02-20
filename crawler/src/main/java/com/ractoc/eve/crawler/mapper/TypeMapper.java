package com.ractoc.eve.crawler.mapper;

import com.ractoc.eve.crawler.model.YamlTypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TypeMapper {
    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    com.ractoc.eve.assets_client.model.TypeModel modelToAssetApi(YamlTypeModel model);
}
