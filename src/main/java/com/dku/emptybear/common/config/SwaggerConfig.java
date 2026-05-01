package com.dku.emptybear.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI emptyBearOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Empty Bear API")
                        .description("빈 강의실 추천 서비스 API 문서")
                        .version("v1.0.0"));
    }
}