package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProductsImpl;
import com.ractoc.eve.domain.assets.BlueprintProductModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintManufacturingProductMapper {
    BlueprintManufacturingProductMapper INSTANCE = Mappers.getMapper(BlueprintManufacturingProductMapper.class);

    BlueprintManufacturingProductsImpl modelToDb(BlueprintProductModel model);

    BlueprintProductModel dbToModel(BlueprintManufacturingProducts materials);
}
