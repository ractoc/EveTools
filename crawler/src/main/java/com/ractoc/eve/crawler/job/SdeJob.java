package com.ractoc.eve.crawler.job;

import com.ractoc.eve.crawler.listener.SdeJobCompletionNotificationListener;
import com.ractoc.eve.crawler.step.BlueprintsStep;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;

public class SdeJob {

    @Autowired
    private SdeJobCompletionNotificationListener listener;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private BlueprintsStep blueprintsStep;

    public Job getJob() {
        return jobBuilderFactory.get("import Static Data Dump Job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(blueprintsStep.getStep())
                .end()
                .build();
    }
}
