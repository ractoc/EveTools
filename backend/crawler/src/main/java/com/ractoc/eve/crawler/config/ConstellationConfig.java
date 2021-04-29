package com.ractoc.eve.crawler.config;

import com.ractoc.eve.crawler.processor.ConstellationItemProcessor;
import com.ractoc.eve.crawler.reader.ConstellationItemReader;
import com.ractoc.eve.crawler.step.ConstellationStep;
import com.ractoc.eve.crawler.writer.ConstellationItemWriter;
import com.ractoc.eve.universe_client.api.ConstellationResourceApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConstellationConfig {

    @Bean
    ConstellationItemReader constellationItemReader() {
        return new ConstellationItemReader();
    }

    @Bean
    ConstellationItemProcessor constellationItemProcessor() {
        return new ConstellationItemProcessor();
    }

    @Bean
    ConstellationItemWriter constellationItemWriter() {
        return new ConstellationItemWriter();
    }

    @Bean
    ConstellationStep constellationStep() {
        return new ConstellationStep();
    }

    @Bean
    ConstellationResourceApi constellationResourceApi() {
        return new ConstellationResourceApi();
    }
}
