package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.crawler.mapper.ConstellationMapper;
import com.ractoc.eve.domain.universe.ConstellationModel;
import com.ractoc.eve.universe_client.ApiException;
import com.ractoc.eve.universe_client.api.ConstellationResourceApi;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class ConstellationItemWriter implements ItemWriter<ConstellationModel> {

    @Autowired
    private ConstellationResourceApi api;

    public void write(List<? extends ConstellationModel> constellations) throws ApiException {
        api.saveConstellation(constellations.stream().map(ConstellationMapper.INSTANCE::modelToAssetApi).collect(Collectors.toList()));
    }
}
