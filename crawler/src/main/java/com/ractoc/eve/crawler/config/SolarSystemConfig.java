package com.ractoc.eve.crawler.config;

import com.ractoc.eve.crawler.processor.SolarsystemItemProcessor;
import com.ractoc.eve.crawler.reader.SolarsystemItemReader;
import com.ractoc.eve.crawler.step.SolarsystemStep;
import com.ractoc.eve.crawler.writer.SolarsystemItemWriter;
import com.ractoc.eve.universe_client.api.SolarsystemResourceApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolarSystemConfig {

    @Bean
    SolarsystemItemReader solarsystemItemReader() {
        return new SolarsystemItemReader();
    }

    @Bean
    SolarsystemItemProcessor solarsystemItemProcessor() {
        return new SolarsystemItemProcessor();
    }

    @Bean
    SolarsystemItemWriter solarsystemItemWriter() {
        return new SolarsystemItemWriter();
    }

    @Bean
    SolarsystemStep solarsystemStep() {
        return new SolarsystemStep();
    }

    @Bean
    SolarsystemResourceApi solarsystemResourceApi() {
        return new SolarsystemResourceApi();
    }

}
