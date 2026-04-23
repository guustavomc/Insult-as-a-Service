package main.java.com.iaas.api.config;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient pythonServiceWebClient(@Value("python-service.base-url") String baseUrl){
        return WebClient.builder().baseUrl(baseUrl).build();
    }

}
