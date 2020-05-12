package com.ractoc.eve.calculator.service;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.BlueprintResourceApi;
import com.ractoc.eve.assets_client.model.BlueprintModel;
import com.ractoc.eve.jesi.api.MarketApi;
import com.ractoc.eve.jesi.model.GetMarketsRegionIdOrders200Ok;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class BlueprintService {

    private final BlueprintResourceApi blueprintResourceApi;

    private final MarketApi marketApi;

    @Autowired
    public BlueprintService(BlueprintResourceApi blueprintResourceApi, MarketApi marketApi) {
        this.blueprintResourceApi = blueprintResourceApi;
        this.marketApi = marketApi;
    }

    public BlueprintModel getBlueprint(Integer bpId) {
        int retryCount = 0;
        while (retryCount < 10) {
            try {
                return blueprintResourceApi.getBlueprint(bpId).getBlueprint();
            } catch (ApiException e) {
                if (e.getCode() != 502) {
                    throw new ServiceException("Unable to retrieve Blueprint: " + bpId, e);
                }
                retryCount++;
            }
        }
        throw new ServiceException("Unable to retrieve Blueprint: " + bpId);
    }

    public double getBlueprintPrice(Integer bpId, Integer regionId, Long locationId) {
        int retryCount = 0;
        do {
            try {
                // sell orders mean buy you minerals, which is done at the lowest possible price.
                // This is why the sellOrder is put into the BuyPrice
                GetMarketsRegionIdOrders200Ok minOrder = getAllOrdersForLocation("sell", regionId, locationId, bpId)
                        .stream()
                        .min(Comparator.comparing(GetMarketsRegionIdOrders200Ok::getPrice))
                        .orElseThrow(() -> new NoSuchElementException("Material not found"));
                return Precision.round(minOrder.getPrice(), 2);
            } catch (NoSuchElementException e) {
                return -1.0;
            } catch (com.ractoc.eve.jesi.ApiException e) {
                if (e.getCode() != 502 || retryCount > 10) {
                    throw new ServiceException("Unable to retrieve sell orders for blueprint " + bpId, e);
                }
                retryCount++;
            }
        } while (retryCount > 0);
        return -1.0;
    }

    private List<GetMarketsRegionIdOrders200Ok> getAllOrdersForLocation(String type, Integer regionId, Long locationId, int itemId) throws com.ractoc.eve.jesi.ApiException {
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
}
