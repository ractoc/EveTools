package com.ractoc.eve.crawler.step;

import com.ractoc.eve.crawler.model.YamlTypeModel;
import com.ractoc.eve.crawler.reader.TypeItemReader;
import com.ractoc.eve.crawler.writer.TypeItemWriter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class TypesStep {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private TypeItemReader reader;
    @Autowired
    private TypeItemWriter writer;

    @Value("${spring.batch.size}")
    private int batchSize;

    public Step getStep() {
        return stepBuilderFactory.get("import Types step")
                .<YamlTypeModel, YamlTypeModel>chunk(batchSize)
                .reader(reader)
                .writer(writer)
                .build();
    }
}
