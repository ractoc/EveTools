package com.ractoc.eve.assets.service;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.Blueprint;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.BlueprintManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlueprintService {

    private BlueprintManager blueprintManager;

    @Autowired
    public BlueprintService(BlueprintManager manager) {
        this.blueprintManager = manager;
    }

    public void saveBlueprint(Blueprint bp) {
            blueprintManager.persist(bp);
    }

    public void clearAllBlueprints() {
            List<Blueprint> bps =  blueprintManager.stream().collect(Collectors.toList());
            bps.stream().forEach(blueprintManager.remover());
    }
}
