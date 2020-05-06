package com.ractoc.eve.crawler.processor;

import com.ractoc.eve.crawler.mapper.RegionMapper;
import com.ractoc.eve.domain.universe.RegionModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.UniverseApi;
import com.ractoc.eve.jesi.model.GetUniverseRegionsRegionIdOk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;

public class RegionItemProcessor implements ItemProcessor<Integer, RegionModel> {

    @Autowired
    private UniverseApi uApi;

    @Value("${esi.retry.timer}")
    private int retry;

    @Override
    public RegionModel process(@NotNull Integer regionId) {
        RegionModel region = null;
        try {
            region = fetchRegionFromAPI(regionId);
        } catch (ApiException e) {
            sleepFor(retry);
            try {
                region = fetchRegionFromAPI(regionId);
            } catch (ApiException ex) {
                throw new ESIProcessorException("Region " + regionId + " not found", e);
            }
        }
        return region;
    }

    private void sleepFor(int timer) {
        try {
            Thread.sleep(timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private RegionModel fetchRegionFromAPI(Integer regionId) throws ApiException {
        GetUniverseRegionsRegionIdOk result = uApi.getUniverseRegionsRegionId(regionId, "en-us", null, null, "en-us");
        return RegionMapper.INSTANCE.uApiToModel(result);
    }
}
