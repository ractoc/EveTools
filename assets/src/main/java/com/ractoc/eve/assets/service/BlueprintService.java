package com.ractoc.eve.assets.service;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.Blueprint;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.BlueprintManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterialsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProductsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkillsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterialsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProductsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkillsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlueprintService {

    private BlueprintManager bpManager;

    private BlueprintInventionMaterialsManager bimManager;
    private BlueprintInventionProductsManager bipManager;
    private BlueprintInventionSkillsManager bisManager;

    private BlueprintManufacturingMaterialsManager bmmManager;
    private BlueprintManufacturingProductsManager bmpManager;
    private BlueprintManufacturingSkillsManager bmsManager;

    @Autowired
    public BlueprintService(BlueprintManager bpManager,
                            BlueprintInventionMaterialsManager bimManager,
                            BlueprintInventionProductsManager bipManager,
                            BlueprintInventionSkillsManager bisManager,
                            BlueprintManufacturingMaterialsManager bmmManager,
                            BlueprintManufacturingProductsManager bmpManager,
                            BlueprintManufacturingSkillsManager bmsManager) {
        this.bpManager = bpManager;

        this.bimManager = bimManager;
        this.bipManager = bipManager;
        this.bisManager = bisManager;

        this.bmmManager = bmmManager;
        this.bmpManager = bmpManager;
        this.bmsManager = bmsManager;
    }

    public void saveBlueprint(Blueprint bp) {
        bpManager.persist(bp);
    }

    public void clearAllBlueprints() {
        List<BlueprintInventionMaterials> bims = bimManager.stream().collect(Collectors.toList());
        bims.stream().forEach(bimManager.remover());
        List<BlueprintInventionProducts> bips = bipManager.stream().collect(Collectors.toList());
        bips.stream().forEach(bipManager.remover());
        List<BlueprintInventionSkills> biss = bisManager.stream().collect(Collectors.toList());
        biss.stream().forEach(bisManager.remover());

        List<BlueprintManufacturingMaterials> bmms = bmmManager.stream().collect(Collectors.toList());
        bmms.stream().forEach(bmmManager.remover());
        List<BlueprintManufacturingProducts> bmps = bmpManager.stream().collect(Collectors.toList());
        bmps.stream().forEach(bmpManager.remover());
        List<BlueprintManufacturingSkills> bmss = bmsManager.stream().collect(Collectors.toList());
        bmss.stream().forEach(bmsManager.remover());

        List<Blueprint> bps = bpManager.stream().collect(Collectors.toList());
        bps.stream().forEach(bpManager.remover());
    }

    public void saveInventionMaterial(BlueprintInventionMaterials bim) {
        bimManager.persist(bim);
    }

    public void saveInventionProduct(BlueprintInventionProducts bip) {
        bipManager.persist(bip);
    }

    public void saveInventionSkill(BlueprintInventionSkills bis) {
        System.out.println("saving: " + bis);
        bisManager.persist(bis);
    }

    public void saveManufacturingMaterial(BlueprintManufacturingMaterials bmm) {
        bmmManager.persist(bmm);
    }

    public void saveManufacturingProduct(BlueprintManufacturingProducts bmp) {
        bmpManager.persist(bmp);
    }

    public void saveManufacturingSkill(BlueprintManufacturingSkills bms) {
        bmsManager.persist(bms);
    }
}
