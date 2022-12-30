package io.wdd.rpc.execute.result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;

import static io.wdd.rpc.execute.result.RedisStreamReaderConfig.REDIS_STREAM_LISTENER_CONTAINER;


@Component
@Slf4j
public class CreateStreamReader implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private AutowireCapableBeanFactory beanFactory;

    private RedisStreamReaderConfig redisStreamReaderConfig;


    private HashMap<String, StreamMessageListenerContainer> REDIS_STREAM_LISTENER_CONTAINER_CACHE = new HashMap<>(16);


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void registerStreamReader(String streamKey) {

        // prepare the environment
        prepareEnv();

        // destroy the REDIS_STREAM_LISTENER_CONTAINER
        destroyStreamReader(streamKey);

        // modify the configuration ==> streamKey
        modifyStreamReader(streamKey);

        // re-create the REDIS_STREAM_LISTENER_CONTAINER
        createStreamReader(streamKey);

    }

    private void prepareEnv(){

        getBeanFactory();

        getRedisStreamConfig();

    }

    private void getRedisStreamConfig() {
        this.redisStreamReaderConfig = applicationContext.getBean("redisStreamReaderConfig", RedisStreamReaderConfig.class);
    }


    private void getBeanFactory(){
        this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    private void createStreamReader(String streamKey) {

        log.debug("start to create the redis stream listener container");
        // create the lazy bean

        StreamMessageListenerContainer streamMessageListenerContainer = applicationContext.getBean(REDIS_STREAM_LISTENER_CONTAINER, StreamMessageListenerContainer.class);

        REDIS_STREAM_LISTENER_CONTAINER_CACHE.put(streamKey, streamMessageListenerContainer);

        // very important
        log.debug("start the listener container");
        streamMessageListenerContainer.start();

    }

    private void modifyStreamReader(String streamKey) {

        log.debug("start to modify the redis stream listener container stream key");
        String oldStreamKey = redisStreamReaderConfig.getStreamKey();

        log.debug("change stream key from [{}] to [{}]", oldStreamKey, streamKey);

        log.debug("start to set the Redis Stream Reader key");
        redisStreamReaderConfig.setStreamKey(streamKey);

    }


    private void destroyStreamReader(String streamKey) {

        log.debug("start to destroy {}", REDIS_STREAM_LISTENER_CONTAINER);

        String oldStreamKey = redisStreamReaderConfig.getStreamKey();

        if (REDIS_STREAM_LISTENER_CONTAINER_CACHE.containsKey(oldStreamKey)) {

            StreamMessageListenerContainer streamMessageListenerContainer = REDIS_STREAM_LISTENER_CONTAINER_CACHE.get(oldStreamKey);

            log.debug("destroyed old redis stream listener container is [ {} ]", streamMessageListenerContainer);


            // double destroy
            beanFactory.destroyBean(streamMessageListenerContainer);
            streamMessageListenerContainer.stop();
            // help gc
            streamMessageListenerContainer = null;
        }


    }
}
