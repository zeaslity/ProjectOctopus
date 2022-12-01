package io.wdd.common.utils;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Configuration
public class OctopusObjectMapperConfig {

    public static Jackson2ObjectMapperBuilderCustomizer common() {

        return jacksonObjectMapperBuilder -> {
            //若POJO对象的属性值为null，序列化时不进行显示
            //jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);

            //针对于Date类型，文本格式化
            jacksonObjectMapperBuilder.simpleDateFormat("yyyy-MM-dd");

            //
            jacksonObjectMapperBuilder.failOnEmptyBeans(false);
            jacksonObjectMapperBuilder.failOnUnknownProperties(false);
            jacksonObjectMapperBuilder.autoDetectFields(true);

            //针对于JDK新时间类。序列化时带有T的问题，自定义格式化字符串
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            jacksonObjectMapperBuilder.modules(javaTimeModule);

        };
    }

}
