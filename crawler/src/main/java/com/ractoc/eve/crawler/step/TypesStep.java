package com.ractoc.eve.crawler.step;

import com.ractoc.eve.crawler.reader.TypeItemReader;
import com.ractoc.eve.crawler.writer.TypeItemWriter;
import com.ractoc.eve.domain.assets.TypeModel;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TypesStep {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private TypeItemReader reader;
    @Autowired
    private TypeItemWriter writer;

    public Step getStep() {
        return stepBuilderFactory.get("import Types step")
                .<TypeModel, TypeModel>chunk(10)
                .reader(reader)
                .writer(writer)
                .build();
    }
}
