package com.ractoc.eve.crawler.step;

import com.ractoc.eve.crawler.processor.ConstellationItemProcessor;
import com.ractoc.eve.crawler.reader.ConstellationItemReader;
import com.ractoc.eve.crawler.writer.ConstellationItemWriter;
import com.ractoc.eve.domain.universe.ConstellationModel;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class ConstellationStep {

    @Autowired
    private  StepBuilderFactory stepBuilderFactory;
    @Autowired
    private ConstellationItemReader reader;
    @Autowired
    private ConstellationItemProcessor processor;
    @Autowired
    private ConstellationItemWriter writer;

    @Value("${spring.batch.size}")
    private int batchSize;

    public Step getStep() {
        return stepBuilderFactory.get("import constellations step")
                .<Integer, ConstellationModel> chunk(batchSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
