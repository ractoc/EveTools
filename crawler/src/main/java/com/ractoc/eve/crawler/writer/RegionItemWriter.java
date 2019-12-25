package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.crawler.mapper.RegionMapper;
import com.ractoc.eve.domain.universe.RegionModel;
import com.ractoc.eve.universe_client.ApiException;
import com.ractoc.eve.universe_client.api.RegionResourceApi;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RegionItemWriter implements ItemWriter<RegionModel> {

    @Autowired
    private RegionResourceApi regionResourceApi;

    public void write(List<? extends RegionModel> regions) {
        regions.stream().map(RegionMapper.INSTANCE::modelToApi).forEach(this::saveRegionViaAPI);
    }

    private void saveRegionViaAPI(com.ractoc.eve.universe_client.model.RegionModel model) {
        try {
            regionResourceApi.createRegion(model);
        } catch (ApiException e) {
            System.err.println("unable to store region " + model.getName() + ". Resulting error: " + e.getMessage());
        }
    }
}
