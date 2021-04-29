package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.crawler.mapper.RegionMapper;
import com.ractoc.eve.domain.universe.RegionModel;
import com.ractoc.eve.universe_client.ApiException;
import com.ractoc.eve.universe_client.api.RegionResourceApi;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class RegionItemWriter implements ItemWriter<RegionModel> {

    @Autowired
    private RegionResourceApi api;

    public void write(List<? extends RegionModel> regions) throws ApiException {
        api.saveRegions(regions.stream().map(RegionMapper.INSTANCE::modelToAssetApi).collect(Collectors.toList()));
    }
}
