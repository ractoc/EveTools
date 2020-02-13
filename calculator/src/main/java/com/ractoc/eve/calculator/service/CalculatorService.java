package com.ractoc.eve.calculator.service;

import com.ractoc.eve.domain.assets.BlueprintMaterialModel;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.domain.assets.ItemModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.IndustryApi;
import com.ractoc.eve.jesi.api.MarketApi;
import com.ractoc.eve.jesi.model.GetIndustrySystems200Ok;
import com.ractoc.eve.jesi.model.GetIndustrySystemsCostIndice;
import com.ractoc.eve.jesi.model.GetMarketsRegionIdOrders200Ok;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CalculatorService {

    @Autowired
    private MarketApi marketApi;

    @Autowired
    private IndustryApi industryApi;

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

    public void calculateJobInstallationCosts(BlueprintModel blueprint, Long locationId) {
        try {
            List<GetIndustrySystems200Ok> systems = industryApi.getIndustrySystems(null, null);
            Float costIndex = systems.stream()
                    .filter(system -> system.getSolarSystemId().intValue() == locationId.intValue())
                    .findAny()
                    .orElseThrow(() -> new ServiceException("Unable to determine Job Fee"))
                    .getCostIndices().stream()
                    .filter(index -> index.getActivity() == GetIndustrySystemsCostIndice.ActivityEnum.MANUFACTURING)
                    .findAny()
                    .orElseThrow(() -> new ServiceException("Unable to determine Job Fee"))
                    .getCostIndex();
            blueprint.setJobInstallationCosts(blueprint.getMineralBuyPrice() * costIndex);
        } catch (ApiException e) {
            throw new ServiceException("Unable to determine Job Fee", e);
        }
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
}
