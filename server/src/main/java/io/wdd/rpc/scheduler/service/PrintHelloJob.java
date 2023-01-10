package io.wdd.rpc.scheduler.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PrintHelloJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println();
        System.out.println("PrintHelloJob被执行了！");
        System.out.println("context = " + context);
        System.out.println();
    }
}
