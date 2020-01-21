package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.assets.handler.TypeHandler;
import com.ractoc.eve.domain.assets.TypeModel;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.util.List;

public class TypeItemWriter implements ItemWriter<TypeModel> {

    @Autowired
    private TypeHandler handler;

    public void write(@NonNull List<? extends TypeModel> types) {
        handler.saveTypes(types);
    }
}
