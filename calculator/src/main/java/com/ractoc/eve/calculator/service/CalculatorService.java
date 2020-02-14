package com.ractoc.eve.calculator.service;

import com.ractoc.eve.domain.assets.BlueprintMaterialModel;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.domain.assets.ItemModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.IndustryApi;
import com.ractoc.eve.jesi.api.MarketApi;
import com.ractoc.eve.jesi.api.SkillsApi;
import com.ractoc.eve.jesi.api.UniverseApi;
import com.ractoc.eve.jesi.model.*;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalculatorService {

    @Autowired
    private MarketApi marketApi;
    @Autowired
    private IndustryApi industryApi;
    @Autowired
    private SkillsApi skillsApi;
    @Autowired
    private UniverseApi universeApi;

    public void calculateMaterialPrices(BlueprintModel bp, Integer regionId, Long locationId, Integer runs) {
        Set<BlueprintMaterialModel> mats = bp.getManufacturingMaterials();
        double totalMineralSellPrice = 0.0;
        double totalMineralBuyPrice = 0.0;
        for (BlueprintMaterialModel mat : mats) {
            getPricesForMaterial(regionId, locationId, mat);
            mat.setCalculatedTotalQuantity(calculateActualQuantity(runs, mat.getQuantity(), bp.getMaterialEfficiency(), 2));
            totalMineralSellPrice += mat.getSellPrice() * mat.getCalculatedTotalQuantity();
            totalMineralBuyPrice += mat.getBuyPrice() * mat.getCalculatedTotalQuantity();
        }
        bp.setMineralSellPrice(totalMineralSellPrice);
        bp.setMineralBuyPrice(totalMineralBuyPrice);
    }

    public void calculateItemPrices(ItemModel item, Integer regionId, Long locationId, Integer runs) {
        getPricesForItem(item, regionId, locationId, runs);
    }

    public void calculateJobInstallationCosts(BlueprintModel blueprint, String token) {
        try {
            Integer systemId = getSystemFromStructure(blueprint.getLocationId(), token);
            List<GetIndustrySystems200Ok> systems = industryApi.getIndustrySystems(null, null);
            Optional<GetIndustrySystems200Ok> system = systems.stream()
                    .filter(s -> s.getSolarSystemId() == systemId)
                    .findAny();
            blueprint.setJobInstallationCosts(calculateJobInitializationCost(blueprint.getMineralBuyPrice(), system));
        } catch (ApiException e) {
            throw new ServiceException("Unable to determine Job Fee", e);
        }
    }

    private Integer getSystemFromStructure(Long locationId, String token) throws ApiException {
        return universeApi.getUniverseStructuresStructureId(locationId, null, null, token).getSolarSystemId();
    }

    private void getPricesForMaterial(Integer regionId, Long locationId, BlueprintMaterialModel mat) {
        int pageNumber = 1;
        do {
            try {
                List<GetMarketsRegionIdOrders200Ok> orders = marketApi.getMarketsRegionIdOrders("all", regionId, null, null, pageNumber, mat.getTypeId());
                if (orders.isEmpty()) {
                    throw new NoSuchElementException("No order found for material: " + mat + " at location " + locationId);
                }
                List<GetMarketsRegionIdOrders200Ok> locationOrder = findOrdersForLocation(orders, locationId);
                // buy orders mean sell you minerals, which is done at the highest possible price.
                // This is why the buyOrder is put into the SellPrice
                locationOrder.stream()
                        .filter(GetMarketsRegionIdOrders200Ok::isIsBuyOrder)
                        .mapToDouble(GetMarketsRegionIdOrders200Ok::getPrice)
                        .max()
                        .ifPresent(mat::setSellPrice);
                // sell orders means buy your minerals, which is done at the lowest possible price
                // This is why the sellOrder it put into the BuyPrice
                locationOrder.stream()
                        .filter(o -> !o.isIsBuyOrder())
                        .mapToDouble(GetMarketsRegionIdOrders200Ok::getPrice)
                        .min()
                        .ifPresent(mat::setBuyPrice);
                if (mat.getSellPrice() != null && mat.getBuyPrice() != null) {
                    return;
                }
                pageNumber++;
            } catch (ApiException e) {
                throw new ServiceException("Unable to retrieve orders for material " + mat, e);
            }
        } while (pageNumber < 10000);
        throw new ServiceException("Maximum number of pages exceeded while requesting an order for material " + mat + " at location " + locationId);
    }

    private void getPricesForItem(ItemModel item, Integer regionId, Long locationId, Integer runs) {
        int pageNumber = 1;
        do {
            try {
                List<GetMarketsRegionIdOrders200Ok> orders = marketApi.getMarketsRegionIdOrders("all", regionId, null, null, pageNumber, item.getId());
                if (orders.isEmpty()) {
                    throw new NoSuchElementException("No order found for item: " + item + " at location " + locationId);
                }
                List<GetMarketsRegionIdOrders200Ok> locationOrder = findOrdersForLocation(orders, locationId);
                // buy orders mean sell you minerals, which is done at the highest possible price.
                // This is why the buyOrder is put into the SellPrice
                locationOrder.stream()
                        .filter(GetMarketsRegionIdOrders200Ok::isIsBuyOrder)
                        .mapToDouble(GetMarketsRegionIdOrders200Ok::getPrice)
                        .max()
                        .ifPresent(price -> item.setSellPrice(price * runs));
                // sell orders means buy your minerals, which is done at the lowest possible price
                // This is why the sellOrder it put into the BuyPrice
                locationOrder.stream()
                        .filter(o -> !o.isIsBuyOrder())
                        .mapToDouble(GetMarketsRegionIdOrders200Ok::getPrice)
                        .min()
                        .ifPresent(price -> item.setBuyPrice(price * runs));
                if (item.getSellPrice() != null && item.getBuyPrice() != null) {
                    return;
                }
                pageNumber++;
            } catch (ApiException e) {
                throw new ServiceException("Unable to retrieve orders for item " + item, e);
            }
        } while (pageNumber < 10000);
        throw new ServiceException("Maximum number of pages exceeded while requesting an order for item " + item + " at location " + locationId);
    }

    private List<GetMarketsRegionIdOrders200Ok> findOrdersForLocation(List<GetMarketsRegionIdOrders200Ok> orders, Long locationId) {
        return orders.stream()
                .filter(o -> o.getSystemId().equals(locationId.intValue()))
                .collect(Collectors.toList());
    }

    private int calculateActualQuantity(int runs, int baseQuantity, int materialEfficiency, int stationEfficiency) {
        return (int) Math.max(runs, Math.ceil(Precision.round(runs * baseQuantity * calculateMaterialModifier(materialEfficiency, stationEfficiency), 2)));
    }

    private double calculateMaterialModifier(double materialEfficiency, double stationEfficiency) {
        return (100.00 - materialEfficiency) / 100.00 * (100.00 - stationEfficiency) / 100.00;
    }

    private double calculateJobInitializationCost(double mineralBuyPrice, Optional<GetIndustrySystems200Ok> system) {
        Float costIndex = system
                .orElseThrow(() -> new ServiceException("Unable to determine Job Fee"))
                .getCostIndices().stream()
                .filter(index -> index.getActivity() == GetIndustrySystemsCostIndice.ActivityEnum.MANUFACTURING)
                .findAny()
                .orElseThrow(() -> new ServiceException("Unable to determine Job Fee"))
                .getCostIndex();
        return mineralBuyPrice * costIndex;
    }

    public void calculateSalesTax(ItemModel item, Integer charId, String token) {
        Map<Skill, Integer> skillLevels = getSkillsForCharacter(charId, token, Skill.ACCOUNTING);
        double salesTax = 0.05 * (1.0 - skillLevels.get(Skill.ACCOUNTING).doubleValue() * 0.11);
        item.setSalesTax(item.getSellPrice() * salesTax);
    }

    private Map<Skill, Integer> getSkillsForCharacter(Integer charId, String token, Skill... skills) {
        Map<Skill, Integer> skillLevels = new HashMap<>();
        try {
            GetCharactersCharacterIdSkillsOk charSkills = skillsApi.getCharactersCharacterIdSkills(charId, null, null, token);
            for (Skill skill : skills) {
                int skillLevel = charSkills.getSkills()
                        .stream()
                        .filter(s -> s.getSkillId() == skill.getSkillId())
                        .mapToInt(GetCharactersCharacterIdSkillsSkill::getActiveSkillLevel)
                        .findFirst()
                        .orElse(0);
                skillLevels.put(skill, skillLevel);
            }
        } catch (ApiException e) {
            throw new ServiceException("Unable to skills for character " + charId, e);
        }
        return skillLevels;
    }

    private enum Skill {
        ACCOUNTING(16622),
        BROKER_RELATIONS(3446);

        private final int skillId;

        Skill(int skillId) {
            this.skillId = skillId;
        }

        public int getSkillId() {
            return skillId;
        }
    }
}
