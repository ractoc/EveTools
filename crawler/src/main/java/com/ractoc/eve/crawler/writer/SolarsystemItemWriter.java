package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.crawler.mapper.SolarsystemMapper;
import com.ractoc.eve.domain.universe.SolarsystemModel;
import com.ractoc.eve.universe_client.ApiException;
import com.ractoc.eve.universe_client.api.SolarsystemResourceApi;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class SolarsystemItemWriter implements ItemWriter<SolarsystemModel> {

    @Autowired
    private SolarsystemResourceApi api;

    public void write(List<? extends SolarsystemModel> solarsystems) throws ApiException {
        api.saveSolarsystems(solarsystems.stream().map(SolarsystemMapper.INSTANCE::modelToAssetApi).collect(Collectors.toList()));
    }
}
