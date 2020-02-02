package com.ractoc.eve.calculator.service;

import com.ractoc.eve.domain.assets.BlueprintMaterialModel;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.MarketApi;
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

    public void calculateMaterialCost(BlueprintModel bp, Integer regionId, Long locationId, Integer materialEfficiency) {
        Set<BlueprintMaterialModel> mats = bp.getManufacturingMaterials();
        for (BlueprintMaterialModel mat : mats) {
            getPricesForMaterial(regionId, locationId, mat);
            // TODO: take the number of runs from the client
            // TODO: determine the stationEfficiency instead of hard coding
            int actualQuantity = calculateActualQuantity(100, mat.getQuantity(), materialEfficiency, 2);

        }
    }

    public double calculateTotalMaterialCost(BlueprintModel bp, int materialEfficiency, int runs) {
        double totalCost = 0.00;
        Set<BlueprintMaterialModel> mats = bp.getManufacturingMaterials();
        for (BlueprintMaterialModel mat : mats) {
            int actualQuantity = calculateActualQuantity(100, mat.getQuantity(), materialEfficiency, 2);
            totalCost += actualQuantity * mat.getBuyPrice();
        }
        return totalCost;

        bp.getManufacturingMaterials().stream().map()

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
                // buy orders mean sell you minerals, which is done at the highest possible price
                locationOrder.stream()
                        .filter(GetMarketsRegionIdOrders200Ok::isIsBuyOrder)
                        .mapToDouble(GetMarketsRegionIdOrders200Ok::getPrice)
                        .max()
                        .ifPresent(mat::setBuyPrice);
                // sell orders means buy your minerals, which is done at the lowest possible price
                locationOrder.stream()
                        .filter(o -> !o.isIsBuyOrder())
                        .mapToDouble(GetMarketsRegionIdOrders200Ok::getPrice)
                        .min()
                        .ifPresent(mat::setSellPrice);
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
