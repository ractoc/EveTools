package com.ractoc.eve.crawler.reader;

import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.UniverseApi;
import com.ractoc.eve.jesi.model.GetUniverseConstellationsConstellationIdOk;
import com.ractoc.eve.universe_client.api.ConstellationResourceApi;
import com.ractoc.eve.universe_client.model.ConstellationModel;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

public class SolarsystemItemReader implements ItemReader<Integer> {

    @Autowired
    private UniverseApi uApi;

    @Autowired
    private ConstellationResourceApi cApi;

    @Value("${esi.retry.timer}")
    private int retry;

    private int nextSystemIndex = 0;
    private List<Integer> solarsystemIds;

    public Integer read() throws com.ractoc.eve.universe_client.ApiException {
        if (solarsystemIdsNotInitialized()) {
            solarsystemIds = fetchSolarsystemIdsFromAPI();
        }

        Integer solarsystemId = null;

        if (nextSystemIndex < solarsystemIds.size()) {
            solarsystemId = solarsystemIds.get(nextSystemIndex);
            nextSystemIndex++;
        }

        return solarsystemId;
    }

    private boolean solarsystemIdsNotInitialized() {
        return this.solarsystemIds == null;
    }

    private List<Integer> fetchSolarsystemIdsFromAPI() throws com.ractoc.eve.universe_client.ApiException {
        return cApi.getConstellationList().getConstellationList()
                .stream()
                .map(ConstellationModel::getId)
                .map(this::getConstellationById)
                .flatMap(constellations -> constellations.getSystems().stream())
                .collect(Collectors.toList());
    }

    private GetUniverseConstellationsConstellationIdOk getConstellationById(Integer constellationId) {
        try {
            return uApi.getUniverseConstellationsConstellationId(constellationId, "en-us", null, null, "en-us");
        } catch (ApiException e) {
            sleepFor(retry);
            try {
                return uApi.getUniverseConstellationsConstellationId(constellationId, "en-us", null, null, "en-us");
            } catch (ApiException ex) {
                throw new SdeReaderException("Constellation " + constellationId + " not found", e);
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
