package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.ItemResourceApi;
import com.ractoc.eve.crawler.mapper.TypeMapper;
import com.ractoc.eve.crawler.model.YamlTypeModel;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class TypeItemWriter implements ItemWriter<YamlTypeModel> {

    @Autowired
    private ItemResourceApi api;

    public void write(List<? extends YamlTypeModel> types) throws ApiException {
        api.saveItems(types.stream()
                .filter(t -> t.isComplete())
                .map(TypeMapper.INSTANCE::modelToAssetApi)
                .collect(Collectors.toList()));
    }
}
