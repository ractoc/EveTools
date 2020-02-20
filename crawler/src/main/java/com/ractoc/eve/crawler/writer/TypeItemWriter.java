package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.ItemResourceApi;
import com.ractoc.eve.crawler.mapper.TypeMapper;
import com.ractoc.eve.crawler.model.YamlTypeModel;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class TypeItemWriter implements ItemWriter<YamlTypeModel> {

    @Autowired
    private ItemResourceApi api;

    public void write(@NonNull List<? extends YamlTypeModel> types) throws ApiException {
        types.stream().filter(t -> t == null).forEach(System.out::println);
        api.saveItems(types.stream()
                .filter(t -> t.isPublished())
                .map(TypeMapper.INSTANCE::modelToAssetApi)
                .collect(Collectors.toList()));
    }
}
