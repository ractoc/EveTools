package com.ractoc.eve.crawler.config;

import com.ractoc.eve.crawler.processor.RegionItemProcessor;
import com.ractoc.eve.crawler.reader.RegionItemReader;
import com.ractoc.eve.crawler.step.RegionStep;
import com.ractoc.eve.crawler.writer.RegionItemWriter;
import com.ractoc.eve.universe_client.api.RegionResourceApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegionConfig {

    @Bean
    RegionItemReader regionItemReader() {
        return new RegionItemReader();
    }

    @Bean
    RegionItemProcessor regionItemProcessor() {
        return new RegionItemProcessor();
    }

    @Bean
    RegionItemWriter regionItemWriter() {
        return new RegionItemWriter();
    }

    @Bean
    RegionStep regionStep() {
        return new RegionStep();
    }

    @Bean
    RegionResourceApi regionResourceApi() {
        return new RegionResourceApi();
    }

}
