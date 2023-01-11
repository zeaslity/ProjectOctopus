package io.wdd.rpc.scheduler.job;

import io.wdd.rpc.status.AgentRuntimeMetricStatus;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;

public class AgentRunMetricStatusJob extends QuartzJobBean {

    @Resource
    AgentRuntimeMetricStatus agentRuntimeMetricStatus;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        // 从JobDetailContext中获取相应的信息
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        System.out.println("jobDataMap = " + jobDataMap);

        int metricRepeatCount = 10;
        int metricRepeatPinch = 5;
        // 执行Agent Metric 状态收集任务
        agentRuntimeMetricStatus.collect(metricRepeatCount, metricRepeatPinch);

        // todo 机构设计状态会被存储至 Redis Stream Key 中
        // AgentTopicName-Metric

    }

}
