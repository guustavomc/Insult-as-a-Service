package com.iaas.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class WebClientConfig {

    @Bean
    public WebClient pythonServiceWebClient(@Value("${python-service.base-url}") String baseUrl){
        return WebClient.builder().baseUrl(baseUrl).build();
    }

}
