package com.ractoc.eve.crawler.reader;

import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.UniverseApi;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RegionItemReader implements ItemReader<Integer> {

    @Autowired
    private UniverseApi uApi;

    private int nextRegionIndex = 0;
    private List<Integer> regionIds;

    public Integer read() {
        if (regionIdsNotInitialized()) {
            regionIds = fetchRegionIdsFromAPI();
        }

        Integer regionId = null;

        if (nextRegionIndex < regionIds.size()) {
            regionId = regionIds.get(nextRegionIndex);
            nextRegionIndex++;
        }

        return regionId;
    }

    private boolean regionIdsNotInitialized() {
        return this.regionIds == null;
    }

    private List<Integer> fetchRegionIdsFromAPI() {
        try {
            return uApi.getUniverseRegions(null, null);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
