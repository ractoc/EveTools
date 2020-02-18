package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.BlueprintResourceApi;
import com.ractoc.eve.crawler.mapper.BlueprintMapper;
import com.ractoc.eve.domain.assets.BlueprintModel;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class BlueprintItemWriter implements ItemWriter<BlueprintModel> {

    @Autowired
    private BlueprintResourceApi api;

    public void write(@NonNull List<? extends BlueprintModel> blueprints) throws ApiException {
        api.saveBlueprints(blueprints.stream().map(BlueprintMapper.INSTANCE::modelToAssetApi).collect(Collectors.toList()));
    }
}
