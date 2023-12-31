package com.example.developerIQ.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GithubRestClientConfig {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate githubRestTemplate() {
        return restTemplateBuilder
                .rootUri("https://api.github.com")
                .defaultHeader("Authorization", "<github token>")
                .build();
    }
}
