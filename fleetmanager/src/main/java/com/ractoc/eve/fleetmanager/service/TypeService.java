package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.types.Types;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.types.TypesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TypeService {

    private final TypesManager typesManager;

    @Autowired
    public TypeService(TypesManager typesManager) {
        this.typesManager = typesManager;
    }

    public Stream<Types> getTypes() {
        return typesManager.stream().sorted(Types.NAME);
    }

    public Optional<Types> getTypesById(Integer typeId) {
        return typesManager.stream().filter(Types.ID.equal(typeId)).findFirst();
    }
}
