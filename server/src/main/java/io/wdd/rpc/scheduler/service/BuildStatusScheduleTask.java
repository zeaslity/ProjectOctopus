package io.wdd.rpc.scheduler.service;


import io.wdd.rpc.scheduler.job.AgentRunMetricStatusJob;
import io.wdd.rpc.scheduler.job.MonitorAllAgentStatusJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import static io.wdd.rpc.status.AgentRuntimeMetricStatus.METRIC_REPORT_TIMES_COUNT;
import static io.wdd.rpc.status.AgentRuntimeMetricStatus.METRIC_REPORT_TIME_PINCH;

@Component
@Slf4j
public class BuildStatusScheduleTask {

    @Resource
    OctopusQuartzService octopusQuartzService;

    @Value(value = "${octopus.status.healthy.cron}")
    String healthyCronTimeExpress;

    @Value(value = "${octopus.status.healthy.start-delay}")
    int healthyCheckStartDelaySeconds;

    @Value(value = "${octopus.status.metric.pinch}")
    int metricReportTimePinch;

    public static final String JOB_GROUP_NAME = "OctopusAgent";

    @PostConstruct
    private void buildAll() {

        // Agent存活健康状态检查
        buildMonitorAllAgentStatusScheduleTask();

        // Agent运行信息检查 Metric

        // Agent全部信息检查 All

    }

    /**
     * Agent运行信息检查 Metric
     * 【调用】应该由 健康状态检查结果 调用 ==> 所有存活节点需要进行Metric信息汇报
     * 【间隔】存活间隔内，间隔一定的时间汇报Metric
     */
    public void buildAgentMetricScheduleTask() {

        // 计算 Metric检测的时间间隔
        int metricReportTimesCount = 19;
        try {
            CronExpression cronExpression = new CronExpression(healthyCronTimeExpress);

            Date now = new Date();
            Date nextValidTime = cronExpression.getNextValidTimeAfter(now);
            long totalSeconds = (nextValidTime.getTime() - now.getTime()) / 1000;
            metricReportTimesCount = (int) (totalSeconds / metricReportTimePinch) - 1;

            System.out.println("totalSeconds = " + totalSeconds);
            System.out.println("metricReportTimesCount = " + metricReportTimesCount);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(METRIC_REPORT_TIME_PINCH,metricReportTimePinch);
        map.put(METRIC_REPORT_TIMES_COUNT,metricReportTimesCount);

        // build the Job 只发送一次消息，然后让Agent获取消息 （重复间隔，重复次数） 进行相应的处理！
        // todo 解决创建太多对象的问题，需要缓存相应的内容
        octopusQuartzService.addJob(
                AgentRunMetricStatusJob.class,
                "agentRunMetricStatusJob",
                JOB_GROUP_NAME,
                metricReportTimePinch,
                1,
                map
        );

    }

    /**
     * Agent存活健康状态检查
     * <p>
     * 定时任务，从Nacos配置中获取相应的信息
     * 延迟触发时间 healthyCheckStartDelaySeconds
     * 定时任务间隔 healthyCronTimeExpress
     */
    private void buildMonitorAllAgentStatusScheduleTask() {

        // build the Job
        octopusQuartzService.addJob(
                MonitorAllAgentStatusJob.class,
                "monitorAllAgentStatusJob",
                JOB_GROUP_NAME,
                healthyCheckStartDelaySeconds,
                healthyCronTimeExpress,
                null
        );

    }


}
