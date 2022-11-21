package io.wdd.server.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = "Bean Type Convert")
public class EntityUtils<T, S> {

    public static <T, S> T cvToTarget(S source, Class<T> clazz) {

        T t = null;

        try {
            t = clazz.getDeclaredConstructor().newInstance();

            BeanUtils.copyProperties(source, t);


        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {

            log.warn("bean type convert error {}", clazz);
        }

        return t;

    }


    public static <T, S> List<T> cvToTarget(List<S> source, Class<T> clazz) {

        return source.stream().map(
                s -> {
                    T t = null;

                    try {
                        t = clazz.getDeclaredConstructor().newInstance();

                        BeanUtils.copyProperties(s, t);

                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {

                        log.warn("bean type convert error {}", clazz);
                    }
                    return t;
                }
        ).collect(Collectors.toList());

    }

}
