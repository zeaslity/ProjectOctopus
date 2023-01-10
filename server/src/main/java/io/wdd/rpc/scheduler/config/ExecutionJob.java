package io.wdd.rpc.scheduler.config;

import io.wdd.common.handler.MyRuntimeException;
import io.wdd.rpc.scheduler.beans.OctopusQuartzJob;
import io.wdd.server.utils.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;


@Async
public class ExecutionJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        //通过JobExecutionContext对象得到OctopusQuartzJob实例。
        OctopusQuartzJob octopusQuartzJob = (OctopusQuartzJob)
                context.getMergedJobDataMap().get(
                        io.wdd.rpc.scheduler.beans.OctopusQuartzJob.JOB_KEY);

        //反射获取到方法，并执行。
        runMethod(octopusQuartzJob.getBeanName(), octopusQuartzJob.getMethodName(), octopusQuartzJob.getParams());
    }

    /***
     * description:反射执行方法
     *
     * @author: zeaslity
     */
    public void runMethod(String beanName, String methodName, String params) {
        Object target = SpringUtils.getBean(beanName);
        Method method = null;

        try {
            //执行的方法只能有两种，有String参数或者无参数，毕竟前端只能传字符串参数给后端。
            if (StringUtils.isNotBlank(params)) {
                //反射获取到方法 两个参数 分别是方法名和参数类型
                method = target.getClass().getDeclaredMethod(methodName, String.class);
            } else {
                method = target.getClass().getDeclaredMethod(methodName);
            }

            //反射执行方法
            ReflectionUtils.makeAccessible(method);

            if (StringUtils.isNotBlank(params)) {
                method.invoke(target, params);
            } else {
                method.invoke(target);
            }
        } catch (Exception e) {
            throw new MyRuntimeException("定时任务执行失败");
        }
    }
}
