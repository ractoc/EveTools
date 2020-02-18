package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.crawler.mapper.MarketHubMapper;
import com.ractoc.eve.domain.universe.MarketHubModel;
import com.ractoc.eve.universe_client.ApiException;
import com.ractoc.eve.universe_client.api.MarketHubResourceApi;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class MarketHubItemWriter implements ItemWriter<MarketHubModel> {

    @Autowired
    private MarketHubResourceApi api;

    public void write(@NonNull List<? extends MarketHubModel> marketHubs) throws ApiException {
        api.saveMarketHubs(marketHubs.stream().map(MarketHubMapper.INSTANCE::modelToAssetApi).collect(Collectors.toList()));
    }
}
