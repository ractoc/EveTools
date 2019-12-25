package com.ractoc.eve.crawler.reader;

import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.UniverseApi;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ConstellationItemReader implements ItemReader<Integer> {

    @Autowired
    private UniverseApi uApi;

    private int nextConstellationIndex = 0;
    private List<Integer> constellationIds;

    public Integer read() {
        if (constellationIdsNotInitialized()) {
            constellationIds = fetchConstellationIdsFromAPI();
        }

        Integer constellationId = null;

        if (nextConstellationIndex < constellationIds.size()) {
            constellationId = constellationIds.get(nextConstellationIndex);
            nextConstellationIndex++;
        }

        return constellationId;
    }

    private boolean constellationIdsNotInitialized() {
        return this.constellationIds == null;
    }

    private List<Integer> fetchConstellationIdsFromAPI() {
        try {
            return uApi.getUniverseConstellations(null, null);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
