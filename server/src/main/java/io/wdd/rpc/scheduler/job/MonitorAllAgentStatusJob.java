package io.wdd.rpc.scheduler.job;

import io.wdd.rpc.scheduler.config.QuartzLogOperator;
import io.wdd.rpc.status.MonitorAllAgentStatus;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;

public class MonitorAllAgentStatusJob extends QuartzJobBean {

    @Resource
    MonitorAllAgentStatus monitorAllAgentStatus;

    @Resource
    QuartzLogOperator quartzLogOperator;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        // get the jobMetaMap
        //JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();

        // actually execute the monitor service
        monitorAllAgentStatus.go();

        // log to somewhere
        quartzLogOperator.save();

    }
}
