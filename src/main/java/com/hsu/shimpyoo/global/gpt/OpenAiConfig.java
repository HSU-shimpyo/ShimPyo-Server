package com.hsu.shimpyoo.global.gpt;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class OpenAiConfig {
    @Value("${gpt.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void addInterceptor() {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + apiKey);
            return execution.execute(request, body);
        });
    }
}
