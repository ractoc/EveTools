package com.ractoc.eve.crawler.reader;

import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.UniverseApi;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class ConstellationItemReader implements ItemReader<Integer> {

    @Autowired
    private UniverseApi uApi;

    @Value("${esi.retry.timer}")
    private int retry;

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
            sleepFor(retry);
            try {
                return uApi.getUniverseConstellations(null, null);
            } catch (ApiException ex) {
                throw new SdeReaderException("Constellations  not found", e);
            }
        }
    }

    private void sleepFor(int timer) {
        try {
            Thread.sleep(timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
