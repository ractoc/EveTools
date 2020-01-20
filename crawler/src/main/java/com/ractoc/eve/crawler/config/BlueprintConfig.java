package com.ractoc.eve.crawler.config;

import com.ractoc.eve.crawler.reader.BlueprintItemReader;
import com.ractoc.eve.crawler.step.BlueprintsStep;
import com.ractoc.eve.crawler.writer.BlueprintItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlueprintConfig {

    @Bean
    BlueprintItemReader blueprintItemReader() {
        return new BlueprintItemReader();
    }

    @Bean
    BlueprintItemWriter blueprintItemWriter() {
        return new BlueprintItemWriter();
    }

    @Bean
    BlueprintsStep blueprintStep() {
        return new BlueprintsStep();
    }
}
