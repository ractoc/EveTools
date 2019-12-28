package com.ractoc.eve.crawler.processor;

import com.ractoc.eve.crawler.mapper.SolarsystemMapper;
import com.ractoc.eve.domain.universe.SolarsystemModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.UniverseApi;
import com.ractoc.eve.jesi.model.GetUniverseSystemsSystemIdOk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;

public class SolarsystemItemProcessor implements ItemProcessor<Integer, SolarsystemModel> {

    @Autowired
    private UniverseApi uApi;

    @Override
    public SolarsystemModel process(@NotNull Integer solarsystemId) {
        return fetchSolarsystemFromAPI(solarsystemId);
    }

    private SolarsystemModel fetchSolarsystemFromAPI(Integer solarsystemId) {
        try {
            GetUniverseSystemsSystemIdOk result = uApi.getUniverseSystemsSystemId(solarsystemId, "en-us", null, null, "en-us");
            return SolarsystemMapper.INSTANCE.uApiToModel(result);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
