package com.ractoc.eve.crawler.step;

import com.ractoc.eve.crawler.processor.SolarsystemItemProcessor;
import com.ractoc.eve.crawler.reader.SolarsystemItemReader;
import com.ractoc.eve.crawler.writer.SolarsystemItemWriter;
import com.ractoc.eve.domain.universe.SolarsystemModel;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class SolarsystemStep {

    @Autowired
    private  StepBuilderFactory stepBuilderFactory;
    @Autowired
    private SolarsystemItemReader reader;
    @Autowired
    private SolarsystemItemProcessor processor;
    @Autowired
    private SolarsystemItemWriter writer;

    @Value("${spring.batch.size}")
    private int batchSize;

    public Step getStep() {
        return stepBuilderFactory.get("import systems step")
                .<Integer, SolarsystemModel> chunk(batchSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
