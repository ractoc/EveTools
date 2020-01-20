package com.ractoc.eve.crawler.step;

import com.ractoc.eve.crawler.reader.BlueprintItemReader;
import com.ractoc.eve.crawler.writer.BlueprintItemWriter;
import com.ractoc.eve.domain.assets.BlueprintModel;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BlueprintsStep {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private BlueprintItemReader reader;
    @Autowired
    private BlueprintItemWriter writer;

    public Step getStep() {
        return stepBuilderFactory.get("import Blueprints step")
                .<BlueprintModel, BlueprintModel>chunk(10)
                .reader(reader)
                .writer(writer)
                .build();
    }
}
