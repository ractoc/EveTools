package com.ractoc.eve.crawler.processor;

import com.ractoc.eve.crawler.mapper.ConstellationMapper;
import com.ractoc.eve.domain.universe.ConstellationModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.UniverseApi;
import com.ractoc.eve.jesi.model.GetUniverseConstellationsConstellationIdOk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class ConstellationItemProcessor implements ItemProcessor<Integer, ConstellationModel> {

    @Autowired
    private UniverseApi uApi;

    @Value("${esi.retry.timer}")
    private int retry;

    @Override
    public ConstellationModel process(Integer constellationId) {
        ConstellationModel constellation;
        try {
            constellation = fetchConstellationFromAPI(constellationId);
        } catch (ApiException e) {
            sleepFor(retry);
            try {
                constellation = fetchConstellationFromAPI(constellationId);
            } catch (ApiException ex) {
                throw new ESIProcessorException("Constellation " + constellationId + " not found", e);
            }
        }
        return constellation;
    }

    private void sleepFor(int timer) {
        try {
            Thread.sleep(timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ConstellationModel fetchConstellationFromAPI(Integer constellationId) throws ApiException {
        GetUniverseConstellationsConstellationIdOk result = uApi.getUniverseConstellationsConstellationId(constellationId, "en-us", null, null, "en-us");
        return ConstellationMapper.INSTANCE.uApiToModel(result);
    }
}
