package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.crawler.mapper.ConstellationMapper;
import com.ractoc.eve.domain.universe.ConstellationModel;
import com.ractoc.eve.universe_client.ApiException;
import com.ractoc.eve.universe_client.api.ConstellationResourceApi;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ConstellationItemWriter implements ItemWriter<ConstellationModel> {

    @Autowired
    private ConstellationResourceApi constellationResourceApi;

    public void write(List<? extends ConstellationModel> constellations) {
        constellations.stream().map(ConstellationMapper.INSTANCE::modelToApi).forEach(this::saveConstellationViaAPI);
    }

    private void saveConstellationViaAPI(com.ractoc.eve.universe_client.model.ConstellationModel model) {
        try {
            constellationResourceApi.createConstellation(model);
        } catch (ApiException e) {
            System.err.println("unable to store constellation " + model.getName() + ". Resulting error: " + e.getMessage());
        }
    }
}
