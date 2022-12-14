package io.wdd.rpc.scheduler.service;

import io.wdd.common.handler.MyRuntimeException;
import io.wdd.rpc.scheduler.beans.OctopusQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.quartz.*;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static io.wdd.rpc.scheduler.service.BuildStatusScheduleTask.JOB_GROUP_NAME;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Andya
 * @date 2021/4/01
 */
@Slf4j
@Service
public class OctopusQuartzServiceImpl implements OctopusQuartzService {

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void startScheduler() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addJob(OctopusQuartzJob quartzJob) {


        return false;
    }

    /**
     * 增加一个job
     *
     * @param jobClass     任务实现类
     * @param jobName      任务名称
     * @param jobGroupName 任务组名
     * @param jobRunTimePinch      时间表达式 (这是每隔多少秒为一次任务)
     * @param jobRunRepeatTimes     运行的次数 （<0:表示不限次数）
     * @param jobData      参数
     */
    @Override
    public void addJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, int jobRunTimePinch, int jobRunRepeatTimes, Map jobData) {
        try {
            // 任务名称和组构成任务key
            JobDetail jobDetail = JobBuilder
                    .newJob(jobClass)
                    .withIdentity(jobName, jobGroupName)
                    .build();

            // 设置job参数
            if (jobData != null && jobData.size() > 0) {
                jobDetail.getJobDataMap().putAll(jobData);
            }

            // 使用simpleTrigger规则
            Trigger trigger = newTrigger()
                    .withIdentity(jobName, jobGroupName)
                    .withSchedule(
                            SimpleScheduleBuilder
                                    .repeatSecondlyForTotalCount(
                                            jobRunRepeatTimes,
                                            jobRunTimePinch
                                    )
                    )
                    .startNow()
                    .build();

            log.debug("jobDataMap: {}", jobDetail.getJobDataMap().getWrappedMap());

            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new MyRuntimeException("add job error!");
        }
    }

    /**
     * 增加一个job
     *
     * @param jobClass          任务实现类
     * @param jobName           任务名称(建议唯一)
     * @param jobGroupName      任务组名
     * @param startTime
     * @param cronJobExpression 时间表达式 （如：0/5 * * * * ? ）
     * @param jobData           参数
     */
    @Override
    public void addJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, int startTime, String cronJobExpression, Map jobData) {
        try {
            // 创建jobDetail实例，绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定job类
            // 任务名称和组构成任务key
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
            // 设置job参数
            if (jobData != null && jobData.size() > 0) {
                jobDetail.getJobDataMap().putAll(jobData);
            }

            // 定义调度触发规则
            // 使用cornTrigger规则
            // 触发器key

            // uniform the start time
            if (ObjectUtils.isEmpty(startTime) || startTime == 0) {
                startTime = 1;
            }

            Trigger trigger = newTrigger()
                    .withIdentity(jobName, jobGroupName)
                    .startAt(
                            DateBuilder.futureDate(startTime, IntervalUnit.SECOND)
                    )
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule(cronJobExpression)
                    )
                    .startNow()
                    .build();

            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("jobDataMap: {}", jobDetail.getJobDataMap());

        } catch (Exception e) {
            e.printStackTrace();
            throw new MyRuntimeException("add job error!");
        }
    }

    /**
     * 修改 一个job的 时间表达式
     *
     * @param jobName
     * @param jobGroupName
     * @param jobTime
     */
    @Override
    public void updateJob(String jobName, String jobGroupName, String jobTime) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            log.info("new jobTime: {}", jobTime);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(CronScheduleBuilder.cronSchedule(jobTime)).build();
            // 重启触发器
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new MyRuntimeException("update job error!");
        }
    }

    /**
     * 删除任务一个job
     *
     * @param jobName      任务名称
     * @param jobGroupName 任务组名
     */
    @Override
    public void deleteJob(String jobName, String jobGroupName) {
        try {
            scheduler.deleteJob(new JobKey(jobName, jobGroupName));
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyRuntimeException("delete job error!");
        }
    }

    /**
     * 暂停一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    @Override
    public void pauseJob(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new MyRuntimeException("pause job error!");
        }
    }

    /**
     * 恢复一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    @Override
    public void resumeJob(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new MyRuntimeException("resume job error!");
        }
    }

    /**
     * 立即执行一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    @Override
    public void runAJobNow(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new MyRuntimeException("run a job error!");
        }
    }

    /**
     * 获取所有计划中的任务列表
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> queryAllJob() {
        List<Map<String, Object>> jobList = null;
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            jobList = new ArrayList<Map<String, Object>>();
            for (JobKey jobKey : jobKeys) {
                log.info("maps: {}", scheduler.getJobDetail(jobKey).getJobDataMap().getWrappedMap());
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("jobName", jobKey.getName());
                    map.put("jobGroupName", jobKey.getGroup());
                    map.put("description", "触发器:" + trigger.getKey());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    map.put("jobStatus", triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        map.put("jobTime", cronExpression);
                    }
                    jobList.add(map);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new MyRuntimeException("query all jobs error!");
        }
        return jobList;
    }

    /**
     * 获取所有正在运行的job
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> queryRunJob() {
        List<Map<String, Object>> jobList = null;
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            jobList = new ArrayList<Map<String, Object>>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                Map<String, Object> map = new HashMap<String, Object>();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                map.put("jobName", jobKey.getName());
                map.put("jobGroupName", jobKey.getGroup());
                map.put("description", "触发器:" + trigger.getKey());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                map.put("jobStatus", triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    map.put("jobTime", cronExpression);
                }
                jobList.add(map);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new MyRuntimeException("query run jobs error!");
        }
        return jobList;
    }

    @Override
    public List<Trigger> queryAllTrigger() {

        try {

            return scheduler.getTriggerKeys(
                    GroupMatcher.groupEquals(JOB_GROUP_NAME)
            ).stream().map(
                    triggerKey -> {
                        try {
                            return scheduler.getTrigger(triggerKey);
                        } catch (SchedulerException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).collect(Collectors.toList());

        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

}
