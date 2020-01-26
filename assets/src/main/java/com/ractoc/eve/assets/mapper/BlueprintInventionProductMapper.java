package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProductsImpl;
import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.BlueprintProductModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintInventionProductMapper extends BaseMapper {
    BlueprintInventionProductMapper INSTANCE = Mappers.getMapper(BlueprintInventionProductMapper.class);

    BlueprintInventionProductsImpl modelToDb(BlueprintProductModel model);

    BlueprintProductModel dbToModel(BlueprintInventionProducts materials);
}
