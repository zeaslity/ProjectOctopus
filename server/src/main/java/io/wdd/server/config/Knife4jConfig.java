package io.wdd.server.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableKnife4j
@EnableSwagger2
public class Knife4jConfig {

    @Bean
    public Docket createRestApi() {
        return  new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .groupName("Server核心业务")
                .select()
                // controller包路径，配置不对的话，找不到
                .apis(
                        RequestHandlerSelectors.basePackage("io.wdd.server.controller")
                )
                .paths(PathSelectors.any())
                .build();

    }

    @Bean
    public Docket createRestApiRPC() {
        return  new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .groupName("Server调用Agent业务")
                .select()
                // controller包路径，配置不对的话，找不到
                .apis(
                        RequestHandlerSelectors.basePackage("io.wdd.rpc.controller")
                )
                .paths(PathSelectors.any())
                .build();

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Octopus Server")
                .description("Octopus Server Knife4j Documents")
                .version("1.0")
                .build();
    }

}
