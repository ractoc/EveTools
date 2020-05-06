package com.ractoc.eve.crawler.step;

import com.ractoc.eve.crawler.model.YamlMarketGroupModel;
import com.ractoc.eve.crawler.reader.MarketGroupItemReader;
import com.ractoc.eve.crawler.writer.MarketGroupItemWriter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class MarketGroupsStep {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private MarketGroupItemReader reader;
    @Autowired
    private MarketGroupItemWriter writer;

    @Value("${spring.batch.size}")
    private int batchSize;

    public Step getStep() {
        return stepBuilderFactory.get("import MarketGroups step")
                .<YamlMarketGroupModel, YamlMarketGroupModel>chunk(batchSize)
                .reader(reader)
                .writer(writer)
                .build();
    }
}
