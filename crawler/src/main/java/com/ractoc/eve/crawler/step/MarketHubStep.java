package com.ractoc.eve.crawler.step;

import com.ractoc.eve.crawler.reader.MarketHubItemReader;
import com.ractoc.eve.crawler.writer.MarketHubItemWriter;
import com.ractoc.eve.domain.universe.MarketHubModel;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class MarketHubStep {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private MarketHubItemReader reader;
    @Autowired
    private MarketHubItemWriter writer;

    @Value("${spring.batch.size}")
    private int batchSize;

    public Step getStep() {
        return stepBuilderFactory.get("import MarketHub step")
                .<MarketHubModel, MarketHubModel>chunk(batchSize)
                .reader(reader)
                .writer(writer)
                .build();
    }
}
