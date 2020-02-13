package com.ractoc.eve.calculator.mapper;


import com.ractoc.eve.assets_client.model.BlueprintMaterialModel;
import com.ractoc.eve.assets_client.model.BlueprintModel;
import com.ractoc.eve.assets_client.model.BlueprintProductModel;
import com.ractoc.eve.domain.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface BlueprintMapper extends BaseMapper {
    BlueprintMapper INSTANCE = Mappers.getMapper(BlueprintMapper.class);

    @Mapping(target = "inventionMaterials", ignore = true)
    @Mapping(target = "inventionProducts", ignore = true)
    @Mapping(target = "inventionSkills", ignore = true)
    @Mapping(source = "manufacturingMaterials", target = "manufacturingMaterials", qualifiedByName = "manufacturingMaterials")
    @Mapping(source = "manufacturingProducts", target = "manufacturingProducts", qualifiedByName = "manufacturingProducts")
    @Mapping(target = "manufacturingSkills", ignore = true)
    com.ractoc.eve.domain.assets.BlueprintModel apiToModel(BlueprintModel apiModel);

    @Named("manufacturingMaterials")
    default Set<com.ractoc.eve.domain.assets.BlueprintMaterialModel> mapManufacturingMaterials(List<BlueprintMaterialModel> apiList) {
        return apiList.stream().map(BlueprintMaterialMapper.INSTANCE::apiToModel).collect(Collectors.toSet());
    }

    @Named("manufacturingProducts")
    default Set<com.ractoc.eve.domain.assets.BlueprintProductModel> mapManufacturingProducts(List<BlueprintProductModel> apiList) {
        return apiList.stream().map(BlueprintProductMapper.INSTANCE::apiToModel).collect(Collectors.toSet());
    }
}
