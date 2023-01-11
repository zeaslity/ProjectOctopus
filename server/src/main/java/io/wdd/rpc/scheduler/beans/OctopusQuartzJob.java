package io.wdd.rpc.scheduler.beans;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Deprecated
@Data
public class OctopusQuartzJob implements Serializable {

        public static final String JOB_KEY = "JOB_KEY";

        @ApiModelProperty(value = "ID")
        private Long id;

        @ApiModelProperty(value = "用于子任务唯一标识", hidden = true)
        private String uuid;

        @ApiModelProperty(value = "任务名称")
        private String jobName;

        @ApiModelProperty(value = "Bean名称")
        private String beanName;

        @ApiModelProperty(value = "方法名称")
        private String methodName;

        @ApiModelProperty(value = "参数")
        private String params;

        @ApiModelProperty(value = "cron表达式")
        private String cronExpression;

        @ApiModelProperty(value = "状态，暂时或启动")
        private Boolean isPause = false;

        @ApiModelProperty(value = "子任务")
        private String subTask;

        @ApiModelProperty(value = "失败后暂停")
        private Boolean pauseAfterFailure;

}
