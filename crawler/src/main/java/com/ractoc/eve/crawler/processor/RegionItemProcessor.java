package com.ractoc.eve.crawler.processor;

import com.ractoc.eve.crawler.mapper.RegionMapper;
import com.ractoc.eve.domain.universe.RegionModel;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.UniverseApi;
import com.ractoc.eve.jesi.model.GetUniverseRegionsRegionIdOk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;

public class RegionItemProcessor implements ItemProcessor<Integer, RegionModel> {

    @Autowired
    private UniverseApi uApi;

    @Override
    public RegionModel process(@NotNull Integer regionId) {
        return fetchRegionFromAPI(regionId);
    }

    private RegionModel fetchRegionFromAPI(Integer regionId) {
        try {
            GetUniverseRegionsRegionIdOk result = uApi.getUniverseRegionsRegionId(regionId, "en-us", null, null, "en-us");
            return RegionMapper.INSTANCE.uApiToModel(result);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
