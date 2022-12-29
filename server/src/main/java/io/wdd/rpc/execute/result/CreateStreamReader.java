package io.wdd.rpc.execute.result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;


@Component
@Slf4j
public class CreateStreamReader implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public void registerStreamReader(String streamKey) {



        Field declaredField = null;
        try {

            log.debug("start to create the redis stream listener container");
            // create the lazy bean
            RedisStreamReaderConfig.MyStreamMessageListenerContainer redisStreamListenerContainer = applicationContext.getBean("redisStreamListenerContainer", RedisStreamReaderConfig.MyStreamMessageListenerContainer.class);

            declaredField = redisStreamListenerContainer.getClass().getDeclaredField("streamKey");

            log.debug("Change Redis Stream Reader from [ {} ] to [ {} ]",declaredField.get(redisStreamListenerContainer), streamKey);


            log.debug("start to set the Redis Stream Reader key");
            declaredField.set(redisStreamListenerContainer, streamKey);


            log.debug("current stream key is [ {} ]",declaredField.get(redisStreamListenerContainer));

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void createStreamReader(String streamKey) {

    }


    private void destroyStreamReader(String streamKey) {

    }
}
