package com.oneny.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    @Bean
    WebClient imageWebClient() {
        return WebClient.create("http://localhost:8788");
    }
}
