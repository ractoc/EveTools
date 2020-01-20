package com.ractoc.eve.crawler.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class SdeJobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Autowired
    private ThreadPoolTaskExecutor executor;

    public void beforeJob(JobExecution jobExecution) {
        System.out.println("starting Static Data Dump job run " + jobExecution.getId());
    }

    public void afterJob(JobExecution jobExecution) {
        if( jobExecution.getStatus() == BatchStatus.COMPLETED ){
            System.out.println("finished Static Data Dump job run " + jobExecution.getId());
        }
        else if(jobExecution.getStatus() == BatchStatus.FAILED){
            System.out.println("failed Static Data Dump job run " + jobExecution.getId());
        }
    }
}
