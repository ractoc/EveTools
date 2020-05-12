package com.ractoc.eve.calculator.service;

import com.ractoc.eve.domain.assets.BlueprintMaterialModel;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.domain.assets.ItemModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.*;
import com.ractoc.eve.jesi.model.*;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalculatorService {

    public static final String UNABLE_TO_DETERMINE_JOB_FEE = "Unable to determine Job Fee";
    public static final String UNABLE_TO_DETERMINE_BROKER_FEE = "Unable to determine Broker Fee";

    private final MarketApi marketApi;
    private final AssetsApi assetsApi;
    private final IndustryApi industryApi;
    private final SkillsApi skillsApi;
    private final UniverseApi universeApi;

    @Autowired
    public CalculatorService(MarketApi marketApi,
                             AssetsApi assetsApi,
                             IndustryApi industryApi,
                             SkillsApi skillsApi,
                             UniverseApi universeApi) {
        this.marketApi = marketApi;
        this.assetsApi = assetsApi;
        this.industryApi = industryApi;
        this.skillsApi = skillsApi;
        this.universeApi = universeApi;

    }

    public void calculateMaterialPrices(BlueprintModel bp, Integer buyRegionId, Long buyLocationId, Integer sellRegionId, Long sellLocationId, Integer runs) {
        Set<BlueprintMaterialModel> mats = bp.getManufacturingMaterials();
        double totalMineralSellPrice = 0.0;
        double totalMineralBuyPrice = 0.0;
        for (BlueprintMaterialModel mat : mats) {
            getBuyPricesForMaterial(buyRegionId, buyLocationId, mat);
            getSellPricesForMaterial(sellRegionId, sellLocationId, mat);
            mat.setCalculatedTotalQuantity(calculateActualQuantity(runs, mat.getQuantity(), bp.getMaterialEfficiency()));
            totalMineralSellPrice += mat.getSellPrice() * mat.getCalculatedTotalQuantity();
            totalMineralBuyPrice += mat.getBuyPrice() * mat.getCalculatedTotalQuantity();
        }
        bp.setMineralSellPrice(Precision.round(totalMineralSellPrice, 2));
        bp.setMineralBuyPrice(Precision.round(totalMineralBuyPrice, 2));
    }

    public GetMarketsRegionIdOrders200Ok calculateItemPrices(ItemModel item, Integer buyRegionId, Long buyLocationId, Integer sellRegionId, Long sellLocationId, Integer runs) {
        getBuyPricesForItem(item, buyRegionId, buyLocationId, runs);
        return getSellPricesForItem(item, sellRegionId, sellLocationId, runs);
    }

    public void calculateJobInstallationCosts(BlueprintModel blueprint, Integer charId, String token) {
        if (blueprint.getLocationId() == null) {
            blueprint.setJobInstallationCosts(-1);
            return;
        }
        int retryCount = 0;
        while (retryCount < 10) {
            try {
                Integer systemId = getSystemFromLocation(charId, blueprint.getLocationId(), token);
                List<GetIndustrySystems200Ok> systems = industryApi.getIndustrySystems(null, null);
                Optional<GetIndustrySystems200Ok> system = systems.stream()
                        .filter(s -> s.getSolarSystemId().intValue() == systemId.intValue())
                        .findAny();
                blueprint.setJobInstallationCosts(calculateJobInitializationCost(blueprint.getMineralBuyPrice(), system.orElseThrow(() -> new ServiceException(UNABLE_TO_DETERMINE_JOB_FEE))));
                return;
            } catch (ApiException e) {
                if (e.getCode() != 502) {
                    throw new ServiceException(UNABLE_TO_DETERMINE_JOB_FEE, e);
                }
                retryCount++;
            }
        }
        throw new ServiceException(UNABLE_TO_DETERMINE_JOB_FEE);
    }

    public void calculateSalesTax(ItemModel item, Integer charId, String token) {
        Map<Skill, Integer> skillLevels = getSkillsForCharacter(charId, token, Skill.ACCOUNTING);
        double salesTax = 0.05 * (1.0 - skillLevels.get(Skill.ACCOUNTING).doubleValue() * 0.11);
        item.setSalesTax(Precision.round(item.getSellPrice() * salesTax, 2));
    }

    public void calculateBrokerFee(ItemModel item, GetMarketsRegionIdOrders200Ok sellOrder, int charId, String token) {
        float percentage;
        try {
            GetUniverseStationsStationIdOk station = universeApi.getUniverseStationsStationId(sellOrder.getLocationId().intValue(), null, null);
            percentage = station.getReprocessingStationsTake();
        } catch (ApiException e) {
            // in case of an error requesting the station, check if it is actually a structure
            try {
                universeApi.getUniverseStructuresStructureId(sellOrder.getLocationId(), null, null, token);
                percentage = 0.01f;
            } catch (ApiException apiException) {
                throw new ServiceException(UNABLE_TO_DETERMINE_BROKER_FEE, e);
            }


        }

        Map<Skill, Integer> skillLevels = getSkillsForCharacter(charId, token, Skill.BROKER_RELATIONS);
        double brokerFee = percentage - (0.003 * skillLevels.get(Skill.BROKER_RELATIONS).doubleValue());
        item.setBrokerFee(Precision.round(item.getSellPrice() * brokerFee, 2));
    }

    private Integer getSystemFromLocation(Integer charId, Long locationId, String token) {
        int pageNumber = 1;
        do {
            Optional<Integer> assetLocation = getSystemFromLocationForPageNumber(charId, locationId, token, pageNumber);
            if (assetLocation.isPresent()) return assetLocation.get();
            pageNumber++;
        } while (pageNumber < 10000);
        throw new ServiceException("Maximum number of pages exceeded while requesting a System from item location " + locationId);
    }

    private Optional<Integer> getSystemFromLocationForPageNumber(Integer charId, Long locationId, String token, int pageNumber) {
        int retryCount = 0;
        while (retryCount < 10) {
            try {
                List<GetCharactersCharacterIdAssets200Ok> assets = assetsApi.getCharactersCharacterIdAssets(charId, null, null, pageNumber, token);
                if (assets.isEmpty()) {
                    throw new NoSuchElementException("No asset found for location: " + locationId);
                }
                OptionalLong assetLocation = assets.stream().filter(a -> a.getItemId().longValue() == locationId.longValue()).mapToLong(GetCharactersCharacterIdAssets200Ok::getLocationId).findFirst();
                if (assetLocation.isPresent()) {
                    return Optional.of(universeApi.getUniverseStructuresStructureId(assetLocation.getAsLong(), null, null, token).getSolarSystemId());
                }
                return Optional.empty();
            } catch (ApiException e) {
                if (e.getCode() != 502) {
                    throw new ServiceException("Unable to retrieve System from item location " + locationId, e);
                }
                retryCount++;
            }
        }
        throw new ServiceException("Unable to retrieve System from item location " + locationId);
    }

    private void getBuyPricesForMaterial(Integer regionId, Long locationId, BlueprintMaterialModel mat) {
        int retryCount = 0;
        do {
            try {
                // sell orders mean buy you minerals, which is done at the lowest possible price.
                // This is why the sellOrder is put into the BuyPrice
                GetMarketsRegionIdOrders200Ok minOrder = getAllOrdersForLocation("sell", regionId, locationId, mat.getTypeId())
                        .stream()
                        .min(Comparator.comparing(GetMarketsRegionIdOrders200Ok::getPrice))
                        .orElseThrow(() -> new NoSuchElementException("Material not found"));
                mat.setBuyPrice(Precision.round(minOrder.getPrice(), 2));
                return;
            } catch (NoSuchElementException e) {
                mat.setBuyPrice(-1.0);
                return;
            } catch (ApiException e) {
                if (e.getCode() != 502 || retryCount > 10) {
                    throw new ServiceException("Unable to retrieve sell orders for material " + mat, e);
                }
                retryCount++;
            }
        } while (retryCount > 0);
            mat.setBuyPrice(-1.0);
    }

    private void getSellPricesForMaterial(Integer regionId, Long locationId, BlueprintMaterialModel mat) {
        int retryCount = 0;
        do {
            try {
                // buy orders means sell your minerals, which is done at the highest possible price
                // This is why the buyOrder it put into the sellPrice
                GetMarketsRegionIdOrders200Ok maxOrder = getAllOrdersForLocation("buy", regionId, locationId, mat.getTypeId())
                        .stream()
                        .max(Comparator.comparing(GetMarketsRegionIdOrders200Ok::getPrice))
                        .orElseThrow(() -> new NoSuchElementException("Material not found"));
                mat.setSellPrice(Precision.round(maxOrder.getPrice(), 2));
                return;
            } catch (NoSuchElementException e) {
                mat.setSellPrice(-1.0);
                return;
            } catch (ApiException e) {
                if (e.getCode() != 502 || retryCount > 10) {
                    throw new ServiceException("Unable to retrieve buy orders for material " + mat, e);
                }
                retryCount++;
            }
        } while (retryCount > 0);
        mat.setSellPrice(-1.0);
    }

    private void getBuyPricesForItem(ItemModel item, Integer regionId, Long locationId, Integer runs) {
        int retryCount = 0;
        do {
            try {
                // sell orders means buy your item, which is done at the lowest possible price
                // This is why the sellOrder it put into the BuyPrice
                GetMarketsRegionIdOrders200Ok minOrder = getAllOrdersForLocation("sell", regionId, locationId, item.getId())
                        .stream()
                        .min(Comparator.comparing(GetMarketsRegionIdOrders200Ok::getPrice))
                        .orElseThrow(() -> new NoSuchElementException("Order not found"));
                item.setBuyPrice(Precision.round(minOrder.getPrice() * runs, 2));
                return;
            } catch (NoSuchElementException e) {
                item.setBuyPrice(-1.0);
                return;
            } catch (ApiException e) {
                if (e.getCode() != 502 || retryCount > 10) {
                    throw new ServiceException("Unable to retrieve sell orders for item " + item, e);
                }
                retryCount++;
            }
        } while (retryCount > 0);
        item.setBuyPrice(-1.0);
    }

    private GetMarketsRegionIdOrders200Ok getSellPricesForItem(ItemModel item, Integer regionId, Long locationId, Integer runs) {
        int retryCount = 0;
        do {
            try {
                // buy orders mean sell you item, which is done at the highest possible price.
                // This is why the buyOrder is put into the SellPrice
                GetMarketsRegionIdOrders200Ok maxOrder = getAllOrdersForLocation("buy", regionId, locationId, item.getId())
                        .stream()
                        .max(Comparator.comparing(GetMarketsRegionIdOrders200Ok::getPrice))
                        .orElseThrow(() -> new NoSuchElementException("Order not found"));
                item.setSellPrice(Precision.round(maxOrder.getPrice() * runs, 2));
                return maxOrder;
            } catch (NoSuchElementException e) {
                item.setSellPrice(-1.0);
                return null;
            } catch (ApiException e) {
                if (e.getCode() != 502 || retryCount > 10) {
                    throw new ServiceException("Unable to retrieve orders for item " + item, e);
                }
                retryCount++;
            }
        } while (retryCount > 0);
        item.setSellPrice(-1.0);
        return null;
    }

    private List<GetMarketsRegionIdOrders200Ok> getAllOrdersForLocation(String type, Integer regionId, Long locationId, int itemId) throws ApiException {
        List<GetMarketsRegionIdOrders200Ok> orders = new ArrayList<>();
        boolean keepSearching = true;
        int pageNumber = 1;
        while (keepSearching) {
            List<GetMarketsRegionIdOrders200Ok> searchResult = marketApi.getMarketsRegionIdOrders(type, regionId, null, null, pageNumber, itemId);
            orders.addAll(searchResult.stream()
                    .filter(o -> o.getSystemId().equals(locationId.intValue()))
                    .collect(Collectors.toList()));
            keepSearching = !searchResult.isEmpty();
            pageNumber++;
        }
        return orders;
    }

    private int calculateActualQuantity(int runs, int baseQuantity, int materialEfficiency) {
        return (int) Math.max(runs, Math.ceil(Precision.round(runs * baseQuantity * calculateMaterialModifier(materialEfficiency), 2)));
    }

    private double calculateMaterialModifier(double materialEfficiency) {
        return (100.00 - materialEfficiency) / 100.00 * (100.00 - 2) / 100.00;
    }

    private double calculateJobInitializationCost(double mineralBuyPrice, GetIndustrySystems200Ok system) {
        Float costIndex = system
                .getCostIndices().stream()
                .filter(index -> index.getActivity() == GetIndustrySystemsCostIndice.ActivityEnum.MANUFACTURING)
                .findAny()
                .orElseThrow(() -> new ServiceException(UNABLE_TO_DETERMINE_JOB_FEE))
                .getCostIndex();
        return Precision.round(mineralBuyPrice * costIndex, 2);
    }

    private Map<Skill, Integer> getSkillsForCharacter(Integer charId, String token, Skill... skills) {
        Map<Skill, Integer> skillLevels = new EnumMap<>(Skill.class);
        int retryCount = 0;
        while (retryCount < 10) {
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
                return skillLevels;
            } catch (ApiException e) {
                if (e.getCode() != 502) {
                    throw new ServiceException("Unable to skills for character " + charId, e);
                }
                retryCount++;
            }
        }
        throw new ServiceException("Unable to skills for character " + charId);
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
