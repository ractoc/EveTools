package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterialsImpl;
import com.ractoc.eve.domain.assets.BlueprintMaterialModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlueprintInventionMaterialMapper {
    BlueprintInventionMaterialMapper INSTANCE = Mappers.getMapper(BlueprintInventionMaterialMapper.class);

    BlueprintInventionMaterialsImpl modelToDb(BlueprintMaterialModel model);

    BlueprintMaterialModel dbToModel(BlueprintInventionMaterials materials);
}
