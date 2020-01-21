package com.ractoc.eve.crawler.writer;

import com.ractoc.eve.assets.handler.BlueprintHandler;
import com.ractoc.eve.domain.assets.BlueprintModel;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.util.List;

public class BlueprintItemWriter implements ItemWriter<BlueprintModel> {

    @Autowired
    private BlueprintHandler handler;

    public void write(@NonNull List<? extends BlueprintModel> blueprints) {
        handler.saveBlueprints(blueprints);
    }
}
