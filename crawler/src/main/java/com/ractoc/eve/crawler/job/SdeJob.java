package com.ractoc.eve.crawler.job;

import com.ractoc.eve.crawler.listener.SdeJobCompletionNotificationListener;
import com.ractoc.eve.crawler.step.*;
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
    @Autowired
    private TypesStep typesStep;
    @Autowired
    private MarketGroupsStep marketGroupsStep;
    @Autowired
    private RegionStep regionStep;
    @Autowired
    private ConstellationStep constellationStep;
    @Autowired
    private SolarsystemStep solarsystemStep;
    @Autowired
    private MarketHubStep marketHubStep;

    public Job getJob() {
        return jobBuilderFactory.get("import Static Data Dump Job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(typesStep.getStep())
//                .flow(blueprintsStep.getStep())
//                .next(typesStep.getStep())
//                .next(marketGroupsStep.getStep())
//                .next(regionStep.getStep())
//                .next(constellationStep.getStep())
//                .next(solarsystemStep.getStep())
//                .next(marketHubStep.getStep())
                .end()
                .build();
    }
}
