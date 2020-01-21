package com.ractoc.eve.assets.mapper;

import com.ractoc.eve.assets.db.assets.eve_assets.type.Type;
import com.ractoc.eve.assets.db.assets.eve_assets.type.TypeImpl;
import com.ractoc.eve.domain.assets.TypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Optional;
import java.util.OptionalInt;

@Mapper
public interface TypeMapper {
    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    TypeImpl modelToDb(TypeModel model);

    TypeModel dbToModel(Type type);

    @SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }

    @SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
    default int unwrapOptionalInt(OptionalInt optional) {
        return optional.orElse(0);
    }
}
