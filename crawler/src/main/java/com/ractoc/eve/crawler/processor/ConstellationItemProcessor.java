package com.ractoc.eve.crawler.processor;

import com.ractoc.eve.crawler.mapper.ConstellationMapper;
import com.ractoc.eve.domain.universe.ConstellationModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.UniverseApi;
import com.ractoc.eve.jesi.model.GetUniverseConstellationsConstellationIdOk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;

public class ConstellationItemProcessor implements ItemProcessor<Integer, ConstellationModel> {

    @Autowired
    private UniverseApi uApi;

    @Override
    public ConstellationModel process(@NotNull Integer constellationId) {
        return fetchConstellationFromAPI(constellationId);
    }

    private ConstellationModel fetchConstellationFromAPI(Integer constellationId) {
        try {
            GetUniverseConstellationsConstellationIdOk result = uApi.getUniverseConstellationsConstellationId(constellationId, "en-us", null, null, "en-us");
            return ConstellationMapper.INSTANCE.uApiToModel(result);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
