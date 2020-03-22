package com.ractoc.eve.assets.service;

import com.ractoc.eve.assets.db.assets.eve_assets.type.Type;
import com.ractoc.eve.assets.db.assets.eve_assets.type.TypeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.ractoc.eve.assets.db.assets.eve_assets.type.generated.GeneratedType.ID;

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

    public String getItemName(int itemId) {
        return typeManager
                .stream()
                .filter(ID.equal(itemId))
                .map(Type::getName)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Item not found, ID: " + itemId));
    }

    public Type getItemById(int itemId) {
        return typeManager
                .stream()
                .filter(ID.equal(itemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Item not found, ID: " + itemId));
    }
}
