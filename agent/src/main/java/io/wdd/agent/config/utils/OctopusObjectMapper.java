package io.wdd.agent.config.utils;


import io.wdd.common.utils.OctopusObjectMapperConfig;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OctopusObjectMapper {

    //注意：该段代码并未覆盖SpringBoot自动装配的ObjectMapper对象，而是加强其配置。
    // use the common config of object mapper
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return OctopusObjectMapperConfig.common();
    }

}
