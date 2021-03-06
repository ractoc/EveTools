package com.ractoc.eve.crawler.step;

import com.ractoc.eve.crawler.processor.RegionItemProcessor;
import com.ractoc.eve.crawler.reader.RegionItemReader;
import com.ractoc.eve.crawler.writer.RegionItemWriter;
import com.ractoc.eve.domain.universe.RegionModel;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class RegionStep {

    @Autowired
    private  StepBuilderFactory stepBuilderFactory;
    @Autowired
    private  RegionItemReader reader;
    @Autowired
    private RegionItemProcessor processor;
    @Autowired
    private  RegionItemWriter writer;

    @Value("${spring.batch.size}")
    private int batchSize;

    public Step getStep() {
        return stepBuilderFactory.get("import regions step")
                .<Integer, RegionModel> chunk(batchSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
