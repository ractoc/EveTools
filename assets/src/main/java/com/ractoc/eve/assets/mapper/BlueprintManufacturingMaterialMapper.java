package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterialsImpl;
import com.ractoc.eve.domain.assets.BlueprintMaterialModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintManufacturingMaterialMapper {
    BlueprintManufacturingMaterialMapper INSTANCE = Mappers.getMapper(BlueprintManufacturingMaterialMapper.class);

    BlueprintManufacturingMaterialsImpl modelToDb(BlueprintMaterialModel model);

    BlueprintMaterialModel dbToModel(BlueprintManufacturingMaterials materials);
}
