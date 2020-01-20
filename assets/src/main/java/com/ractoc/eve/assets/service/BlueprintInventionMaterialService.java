package com.ractoc.eve.assets.service;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterialsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlueprintInventionMaterialService {

    private BlueprintInventionMaterialsManager manager;

    @Autowired
    public BlueprintInventionMaterialService(BlueprintInventionMaterialsManager manager) {
        this.manager = manager;
    }

    public void saveMaterial(BlueprintInventionMaterials bim) {
            manager.persist(bim);
    }
}
