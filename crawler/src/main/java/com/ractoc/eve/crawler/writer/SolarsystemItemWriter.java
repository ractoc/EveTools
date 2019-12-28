package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.crawler.mapper.SolarsystemMapper;
import com.ractoc.eve.domain.universe.SolarsystemModel;
import com.ractoc.eve.universe_client.ApiException;
import com.ractoc.eve.universe_client.api.SolarsystemResourceApi;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SolarsystemItemWriter implements ItemWriter<SolarsystemModel> {

    @Autowired
    private SolarsystemResourceApi solarsystemResourceApi;

    public void write(List<? extends SolarsystemModel> solarsystems) {
        solarsystems.stream().map(SolarsystemMapper.INSTANCE::modelToApi).forEach(this::saveSolarsystemViaAPI);
    }

    private void saveSolarsystemViaAPI(com.ractoc.eve.universe_client.model.SystemModel model) {
        try {
            solarsystemResourceApi.createSolarsystem(model);
        } catch (ApiException e) {
            System.err.println("unable to store solarsystem " + model.getName() + ". Resulting error: " + e.getMessage());
        }
    }
}
