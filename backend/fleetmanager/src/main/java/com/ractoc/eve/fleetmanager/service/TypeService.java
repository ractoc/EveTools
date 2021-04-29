package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.type.Type;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.type.TypeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TypeService {

    private final TypeManager typeManager;

    @Autowired
    public TypeService(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    public Stream<Type> getTypes() {
        return typeManager.stream().sorted(Type.NAME);
    }

    public Optional<Type> getTypesById(Integer typeId) {
        return typeManager.stream().filter(Type.ID.equal(typeId)).findFirst();
    }
}
