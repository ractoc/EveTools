package com.ractoc.eve.crawler.job;

import com.ractoc.eve.crawler.listener.UniverseJobCompletionNotificationListener;
import com.ractoc.eve.crawler.step.ConstellationStep;
import com.ractoc.eve.crawler.step.RegionStep;
import com.ractoc.eve.crawler.step.SolarsystemStep;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;

public class UniverseJob {

    @Autowired
    private UniverseJobCompletionNotificationListener listener;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private RegionStep regionStep;
    @Autowired
    private ConstellationStep constellationStep;
    @Autowired
    private SolarsystemStep solarsystemStep;

    public Job getJob() {
        return jobBuilderFactory.get("import Universe Job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(regionStep.getStep())
                .next(constellationStep.getStep())
                .next(solarsystemStep.getStep())
                .end()
                .build();
    }
}
