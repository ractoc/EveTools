package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.type.Type;
import com.ractoc.eve.assets.db.assets.eve_assets.type.TypeImpl;
import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.assets.TypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public interface TypeMapper extends BaseMapper {
    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    @Mapping(source="volume", target="volume", qualifiedByName="volumeToDbConverter")
    TypeImpl modelToDb(TypeModel model);

    @Mapping(source="volume", target="volume", qualifiedByName="volumeToModelConverter")
    TypeModel dbToModel(Type type);

    @Named("volumeToModelConverter")
    default double mapVolumeToModel(BigDecimal volIn) {
        System.out.println("mapping " + volIn + " to " + volIn.doubleValue());
        return volIn.doubleValue();
    }

    @Named("volumeToDbConverter")
    default BigDecimal mapVolumeToDb(double volIn) {
        System.out.println("mapping " + volIn + " to " + BigDecimal.valueOf(volIn));
        return BigDecimal.valueOf(volIn);
    }
}
