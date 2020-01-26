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
import java.util.NoSuchElementException;
import java.util.Set;
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
        bims.forEach(bimManager.remover());
        List<BlueprintInventionProducts> bips = bipManager.stream().collect(Collectors.toList());
        bips.forEach(bipManager.remover());
        List<BlueprintInventionSkills> biss = bisManager.stream().collect(Collectors.toList());
        biss.forEach(bisManager.remover());

        List<BlueprintManufacturingMaterials> bmms = bmmManager.stream().collect(Collectors.toList());
        bmms.forEach(bmmManager.remover());
        List<BlueprintManufacturingProducts> bmps = bmpManager.stream().collect(Collectors.toList());
        bmps.forEach(bmpManager.remover());
        List<BlueprintManufacturingSkills> bmss = bmsManager.stream().collect(Collectors.toList());
        bmss.forEach(bmsManager.remover());

        List<Blueprint> bps = bpManager.stream().collect(Collectors.toList());
        bps.forEach(bpManager.remover());
    }

    public void saveInventionMaterial(BlueprintInventionMaterials bim) {
        bimManager.persist(bim);
    }

    public void saveInventionProduct(BlueprintInventionProducts bip) {
        bipManager.persist(bip);
    }

    public void saveInventionSkill(BlueprintInventionSkills bis) {
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

    public Blueprint getBlueprint(Integer bpId) {
        return bpManager
                .stream()
                .filter(Blueprint.ID.equal(bpId))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Unable to find blueprint with id " + bpId));
    }

    public Set<BlueprintInventionMaterials> getInventionMaterials(Integer bpId) {
        return bimManager
                .stream()
                .filter(BlueprintInventionMaterials.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Set<BlueprintInventionProducts> getInventionProducts(Integer bpId) {
        return bipManager
                .stream()
                .filter(BlueprintInventionProducts.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Set<BlueprintInventionSkills> getInventionSkills(Integer bpId) {
        return bisManager
                .stream()
                .filter(BlueprintInventionSkills.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Set<BlueprintManufacturingMaterials> getManufacturingMaterials(Integer bpId) {
        return bmmManager
                .stream()
                .filter(BlueprintManufacturingMaterials.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Set<BlueprintManufacturingProducts> getManufacturingProducts(Integer bpId) {
        return bmpManager
                .stream()
                .filter(BlueprintManufacturingProducts.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Set<BlueprintManufacturingSkills> getManufacturingSkills(Integer bpId) {
        return bmsManager
                .stream()
                .filter(BlueprintManufacturingSkills.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }
}
