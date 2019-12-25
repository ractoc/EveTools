package com.ractoc.eve.crawler.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class UniverseJobCompletionNotificationListener extends JobExecutionListenerSupport {

    public void beforeJob(JobExecution jobExecution) {
        System.out.println("starting job run " + jobExecution.getId());
    }

    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            System.out.println("finished job run " + jobExecution.getId());
        }
    }
}
