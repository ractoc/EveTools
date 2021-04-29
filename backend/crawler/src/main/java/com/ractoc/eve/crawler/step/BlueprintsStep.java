package com.ractoc.eve.crawler.step;

import com.ractoc.eve.crawler.model.YamlBlueprintModel;
import com.ractoc.eve.crawler.reader.BlueprintItemReader;
import com.ractoc.eve.crawler.writer.BlueprintItemWriter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class BlueprintsStep {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private BlueprintItemReader reader;
    @Autowired
    private BlueprintItemWriter writer;

    @Value("${spring.batch.size}")
    private int batchSize;

    public Step getStep() {
        return stepBuilderFactory.get("import Blueprints step")
                .<YamlBlueprintModel, YamlBlueprintModel>chunk(batchSize)
                .reader(reader)
                .writer(writer)
                .build();
    }
}
