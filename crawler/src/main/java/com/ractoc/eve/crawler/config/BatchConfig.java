package com.ractoc.eve.crawler.config;

import com.ractoc.eve.crawler.job.SdeJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    SdeJob sdeJob() {
        return new SdeJob();
    }

    @Bean
    public Job importSdeJob(SdeJob sdeJob) {
        return sdeJob.getJob();
    }
}
