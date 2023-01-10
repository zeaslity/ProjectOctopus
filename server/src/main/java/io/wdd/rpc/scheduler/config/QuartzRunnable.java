package io.wdd.rpc.scheduler.config;

import io.wdd.server.utils.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class QuartzRunnable implements Callable<Object> {

    private final Object target;
    private final Method method;
    private final String params;

    QuartzRunnable(String beanName, String methodName, String params) throws NoSuchMethodException, ClassNotFoundException {

        //获取到bean对象
        this.target = SpringUtils.getBean(beanName);
        //获取到参数
        this.params = params;
        //如果参数不为空
        if (StringUtils.isNotBlank(params)) {
            //反射获取到方法 两个参数 分别是方法名和参数类型
            this.method = target.getClass().getDeclaredMethod(methodName, String.class);
        } else {
            this.method = target.getClass().getDeclaredMethod(methodName);
        }
    }

    /***
     * description: 线程回调函数 反射执行方法
     *
     * @author: lixiangxiang
     */
    @Override
    public Object call() throws Exception {

        ReflectionUtils.makeAccessible(method);

        if (StringUtils.isNotBlank(params)) {
            method.invoke(target, params);
        } else {
            method.invoke(target);
        }
        return null;
    }

}
