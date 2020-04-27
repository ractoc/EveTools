package com.ractoc.eve.assets.service;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.Blueprint;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.BlueprintManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterialsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.generated.GeneratedBlueprintInventionMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProductsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.generated.GeneratedBlueprintInventionProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkillsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.generated.GeneratedBlueprintInventionSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterialsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.generated.GeneratedBlueprintManufacturingMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProductsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.generated.GeneratedBlueprintManufacturingProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkillsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.generated.GeneratedBlueprintManufacturingSkills;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import com.ractoc.eve.jesi.api.CorporationApi;
import com.ractoc.eve.jesi.model.GetCharactersCharacterIdBlueprints200Ok;
import com.ractoc.eve.jesi.model.GetCharactersCharacterIdOk;
import com.ractoc.eve.jesi.model.GetCorporationsCorporationIdBlueprints200Ok;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ractoc.eve.assets.db.assets.eve_assets.blueprint.generated.GeneratedBlueprint.ID;

@Service
public class BlueprintService {

    private final BlueprintManager bpManager;

    private final BlueprintInventionMaterialsManager bimManager;
    private final BlueprintInventionProductsManager bipManager;
    private final BlueprintInventionSkillsManager bisManager;

    private final BlueprintManufacturingMaterialsManager bmmManager;
    private final BlueprintManufacturingProductsManager bmpManager;
    private final BlueprintManufacturingSkillsManager bmsManager;

    private final CharacterApi characterApi;
    private final CorporationApi corporationApi;

    @Autowired
    public BlueprintService(BlueprintManager bpManager,
                            BlueprintInventionMaterialsManager bimManager,
                            BlueprintInventionProductsManager bipManager,
                            BlueprintInventionSkillsManager bisManager,
                            BlueprintManufacturingMaterialsManager bmmManager,
                            BlueprintManufacturingProductsManager bmpManager,
                            BlueprintManufacturingSkillsManager bmsManager,
                            CharacterApi characterApi,
                            CorporationApi corporationApi) {
        this.bpManager = bpManager;

        this.bimManager = bimManager;
        this.bipManager = bipManager;
        this.bisManager = bisManager;

        this.bmmManager = bmmManager;
        this.bmpManager = bmpManager;
        this.bmsManager = bmsManager;

        this.characterApi = characterApi;
        this.corporationApi = corporationApi;
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
                .filter(ID.equal(bpId))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Unable to find blueprint with id " + bpId));
    }

    public Set<BlueprintInventionMaterials> getInventionMaterials(Integer bpId) {
        return bimManager
                .stream()
                .filter(GeneratedBlueprintInventionMaterials.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Set<BlueprintInventionProducts> getInventionProducts(Integer bpId) {
        return bipManager
                .stream()
                .filter(GeneratedBlueprintInventionProducts.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Set<BlueprintInventionSkills> getInventionSkills(Integer bpId) {
        return bisManager
                .stream()
                .filter(GeneratedBlueprintInventionSkills.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Set<BlueprintManufacturingMaterials> getManufacturingMaterials(Integer bpId) {
        return bmmManager
                .stream()
                .filter(GeneratedBlueprintManufacturingMaterials.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Set<BlueprintManufacturingProducts> getManufacturingProducts(Integer bpId) {
        return bmpManager
                .stream()
                .filter(GeneratedBlueprintManufacturingProducts.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Set<BlueprintManufacturingSkills> getManufacturingSkills(Integer bpId) {
        return bmsManager
                .stream()
                .filter(GeneratedBlueprintManufacturingSkills.BLUEPRINT_ID.equal(bpId))
                .collect(Collectors.toSet());
    }

    public Stream<GetCharactersCharacterIdBlueprints200Ok> getBlueprintsForCharacter(Integer characterId, String accessToken) {
        int retryCount = 0;
        while (retryCount < 10) {
            try {
                return characterApi.getCharactersCharacterIdBlueprints(characterId, null, null, 1, accessToken).stream();
            } catch (ApiException e) {
                if (e.getCode() == 403) {
                    throw new AccessDeniedException("Access to the EVE SSO has been denied", e);
                } else if (e.getCode() != 502) {
                    throw new ServiceException(String.format("Unable to retrieve Character Blueprints for character ID %d", characterId), e);
                }
                retryCount++;
            }
        }
        throw new ServiceException(String.format("Unable to retrieve Character Blueprints for character ID %d", characterId));
    }

    public Stream<GetCorporationsCorporationIdBlueprints200Ok> getBlueprintsForCorporation(int characterId, String accessToken) {
        int retryCount = 0;
        while (retryCount < 10) {
            try {
                GetCharactersCharacterIdOk charInfo = characterApi.getCharactersCharacterId(characterId, null, null);
                return corporationApi.getCorporationsCorporationIdBlueprints(charInfo.getCorporationId(), null, null, 1, accessToken).stream();
            } catch (ApiException e) {
                if (e.getCode() == 403) {
                    throw new AccessDeniedException("Access to the EVE SSO has been denied", e);
                } else if (e.getCode() != 502) {
                    throw new ServiceException("Unable to retrieve Character Blueprints for character ID " + characterId, e);
                }
                retryCount++;
            }
        }
        throw new ServiceException("Unable to retrieve Character Blueprints for character ID " + characterId);
    }
}
