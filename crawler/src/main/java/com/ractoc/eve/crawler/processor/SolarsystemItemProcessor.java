package com.ractoc.eve.crawler.processor;

import com.ractoc.eve.crawler.mapper.SolarsystemMapper;
import com.ractoc.eve.domain.universe.SolarsystemModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.UniverseApi;
import com.ractoc.eve.jesi.model.GetUniverseSystemsSystemIdOk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;

public class SolarsystemItemProcessor implements ItemProcessor<Integer, SolarsystemModel> {

    @Autowired
    private UniverseApi uApi;

    @Value("${esi.retry.timer}")
    private int retry;

    @Override
    public SolarsystemModel process(@NotNull Integer solarsystemId) {
        SolarsystemModel system = null;
        try {
            system = fetchSolarsystemFromAPI(solarsystemId);
        } catch (ApiException e) {
            sleepFor(retry);
            try {
                system = fetchSolarsystemFromAPI(solarsystemId);
            } catch (ApiException ex) {
                throw new ESIProcessorException("Solarsystem " + solarsystemId + " not found", e);
            }
        }
        return system;
    }

    private void sleepFor(int timer) {
        try {
            Thread.sleep(timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private SolarsystemModel fetchSolarsystemFromAPI(Integer solarsystemId) throws ApiException {
        GetUniverseSystemsSystemIdOk result = uApi.getUniverseSystemsSystemId(solarsystemId, "en-us", null, null, "en-us");
        return SolarsystemMapper.INSTANCE.uApiToModel(result);
    }
}
