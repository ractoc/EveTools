package com.ractoc.eve.assets.service;

import com.ractoc.eve.assets.db.assets.eve_assets.type.Type;
import com.ractoc.eve.assets.db.assets.eve_assets.type.TypeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TypeService {

    private TypeManager typeManager;

    @Autowired
    public TypeService(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    public void saveType(Type type) {
        typeManager.persist(type);
    }

    public void clearAllTypes() {
        List<Type> types = typeManager.stream().collect(Collectors.toList());
        types.forEach(typeManager.remover());
    }
}
