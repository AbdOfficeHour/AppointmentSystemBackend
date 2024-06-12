package io.github.abdofficehour.appointmentsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("阿伯丁预约平台接口文档")
                        .description("阿伯丁预约平台接口文档")
                        .version("1.1")
                );
    }

}
