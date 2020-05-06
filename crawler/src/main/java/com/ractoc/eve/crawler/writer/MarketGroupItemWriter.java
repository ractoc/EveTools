package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.MarketGroupResourceApi;
import com.ractoc.eve.crawler.mapper.MarketGroupMapper;
import com.ractoc.eve.crawler.model.YamlMarketGroupModel;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class MarketGroupItemWriter implements ItemWriter<YamlMarketGroupModel> {

    @Autowired
    private MarketGroupResourceApi api;

    public void write(List<? extends YamlMarketGroupModel> marketGroups) throws ApiException {
        api.saveMarketGroups(marketGroups.stream()
                .peek(System.out::println)
                .map(MarketGroupMapper.INSTANCE::modelToAssetApi)
                .peek(System.out::println)
                .collect(Collectors.toList()));
    }
}
