package com.ractoc.eve.crawler.config;

import com.ractoc.eve.crawler.job.UniverseJob;
import com.ractoc.eve.crawler.processor.ConstellationItemProcessor;
import com.ractoc.eve.crawler.processor.RegionItemProcessor;
import com.ractoc.eve.crawler.processor.SolarsystemItemProcessor;
import com.ractoc.eve.crawler.reader.ConstellationItemReader;
import com.ractoc.eve.crawler.reader.RegionItemReader;
import com.ractoc.eve.crawler.reader.SolarsystemItemReader;
import com.ractoc.eve.crawler.step.ConstellationStep;
import com.ractoc.eve.crawler.step.RegionStep;
import com.ractoc.eve.crawler.step.SolarsystemStep;
import com.ractoc.eve.crawler.writer.ConstellationItemWriter;
import com.ractoc.eve.crawler.writer.RegionItemWriter;
import com.ractoc.eve.crawler.writer.SolarsystemItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

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
    UniverseJob universeJob() {
        return new UniverseJob();
    }

    @Bean
    public Job importUniverseJob(UniverseJob universeJob) {
        return universeJob.getJob();
    }
}
