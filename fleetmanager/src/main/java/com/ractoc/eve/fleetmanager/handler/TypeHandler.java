package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.TypeModel;
import com.ractoc.eve.fleetmanager.mapper.TypeMapper;
import com.ractoc.eve.fleetmanager.service.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class TypeHandler {

    private final TypeService typeService;

    public TypeHandler(TypeService typeService) {
        this.typeService = typeService;
    }

    public List<TypeModel> getTypes() {
        return typeService.getTypes().map(TypeMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }
}
