package io.wdd.rpc.scheduler.service;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import io.wdd.rpc.scheduler.job.MonitorAllAgentStatusJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Slf4j
public class BuildStatusScheduleTask {

    @Resource
    OctopusQuartzService octopusQuartzService;

    @Value(value = "${octopus.status.healthy.cron}")
    String healthyCronTimeExpress;

    @Value(value = "${octopus.status.healthy.start-delay}")
    int healthyCheckStartDelaySeconds;


    @PostConstruct
    public void buildAll(){

        buildMonitorAllAgentStatusScheduleTask();

    }

    public void buildMonitorAllAgentStatusScheduleTask(){


        // build the Job
        octopusQuartzService.addJob(
                MonitorAllAgentStatusJob.class,
                "monitorAllAgentStatusJob",
                "monitorAllAgentStatusJob",
                healthyCheckStartDelaySeconds,
                healthyCronTimeExpress,
                null
        );



    }
}
